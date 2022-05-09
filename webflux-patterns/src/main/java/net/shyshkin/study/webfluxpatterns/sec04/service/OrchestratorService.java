package net.shyshkin.study.webfluxpatterns.sec04.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.sec04.dto.*;
import net.shyshkin.study.webfluxpatterns.sec04.util.DebugUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrchestratorService {

    private final OrderFulfillmentService fulfillmentService;
    private final OrderCancellationService cancellationService;

    public Mono<OrderResponse> placeOrder(Mono<OrderRequest> mono) {

        return mono.map(OrchestrationRequestContext::new)
                .flatMap(fulfillmentService::placeOrder)
                .doOnNext(this::doOrderPostProcessing)
                .doOnNext(ctx -> DebugUtil.print(log, ctx))
                .map(this::toOrderResponse);
    }

    private void doOrderPostProcessing(OrchestrationRequestContext ctx) {
        if (Status.FAILED.equals(ctx.getStatus()))
            cancellationService.cancelOrder(ctx);
    }

    private OrderResponse toOrderResponse(OrchestrationRequestContext ctx) {
        boolean isSuccess = Status.SUCCESS.equals(ctx.getStatus());
        Address address = isSuccess ? ctx.getShippingResponse().getAddress() : null;
        LocalDate expectedDelivery = isSuccess ? ctx.getShippingResponse().getExpectedDelivery() : null;
        return OrderResponse.builder()
                .userId(ctx.getOrderRequest().getUserId())
                .productId(ctx.getOrderRequest().getProductId())
                .orderId(ctx.getOrderId())
                .address(address)
                .expectedDelivery(expectedDelivery)
                .status(ctx.getStatus())
                .build();
    }

}
