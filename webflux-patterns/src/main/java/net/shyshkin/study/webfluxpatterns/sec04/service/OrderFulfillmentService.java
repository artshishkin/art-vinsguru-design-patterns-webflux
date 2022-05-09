package net.shyshkin.study.webfluxpatterns.sec04.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.sec04.client.ProductClient;
import net.shyshkin.study.webfluxpatterns.sec04.dto.OrchestrationRequestContext;
import net.shyshkin.study.webfluxpatterns.sec04.dto.Product;
import net.shyshkin.study.webfluxpatterns.sec04.dto.Status;
import net.shyshkin.study.webfluxpatterns.sec04.util.OrchestrationUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderFulfillmentService {

    private final ProductClient productClient;
    private final PaymentOrchestrator paymentOrchestrator;
    private final InventoryOrchestrator inventoryOrchestrator;
    private final ShippingOrchestrator shippingOrchestrator;

    public Mono<OrchestrationRequestContext> placeOrder(OrchestrationRequestContext ctx) {

        return this.getProduct(ctx)
                .doOnNext(OrchestrationUtil::buildPaymentRequest)
                .flatMap(paymentOrchestrator::create)
                .doOnNext(OrchestrationUtil::buildInventoryRequest)
                .flatMap(inventoryOrchestrator::create)
                .doOnNext(OrchestrationUtil::buildShippingRequest)
                .flatMap(shippingOrchestrator::create)
                .doOnNext(ctx1 -> ctx1.setStatus(Status.SUCCESS))
                .doOnError(ex -> ctx.setStatus(Status.FAILED))
                .onErrorReturn(ctx);
    }

    private Mono<OrchestrationRequestContext> getProduct(OrchestrationRequestContext ctx) {
        Integer productId = ctx.getOrderRequest().getProductId();
        return productClient
                .getProduct(productId)
                .map(Product::getPrice)
                .doOnNext(ctx::setProductPrice)
                .map(i -> ctx);
    }

}
