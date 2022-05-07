package net.shyshkin.study.webfluxpatterns.sec03.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webfluxpatterns.sec03.client.ProductClient;
import net.shyshkin.study.webfluxpatterns.sec03.dto.*;
import net.shyshkin.study.webfluxpatterns.sec03.util.OrchestrationUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OrchestratorService {

    private final ProductClient productClient;
    private final OrderFulfillmentService fulfillmentService;
    private final OrderCancellationService cancellationService;

    public Mono<OrderResponse> placeOrder(Mono<OrderRequest> mono) {

        return mono.map(OrchestrationRequestContext::new)
                .flatMap(this::getProduct)
                .doOnNext(OrchestrationUtil::buildRequestContext)
                .flatMap(fulfillmentService::placeOrder)
                .doOnNext(this::doOrderPostProcessing)
                .map(this::toOrderResponse);
    }

    private Mono<OrchestrationRequestContext> getProduct(OrchestrationRequestContext ctx) {
        Integer productId = ctx.getOrderRequest().getProductId();
        return productClient
                .getProduct(productId)
                .map(Product::getPrice)
                .doOnNext(ctx::setProductPrice)
                .thenReturn(ctx);
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
