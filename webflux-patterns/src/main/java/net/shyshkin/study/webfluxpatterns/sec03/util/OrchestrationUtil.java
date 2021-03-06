package net.shyshkin.study.webfluxpatterns.sec03.util;

import net.shyshkin.study.webfluxpatterns.sec03.dto.InventoryRequest;
import net.shyshkin.study.webfluxpatterns.sec03.dto.OrchestrationRequestContext;
import net.shyshkin.study.webfluxpatterns.sec03.dto.PaymentRequest;
import net.shyshkin.study.webfluxpatterns.sec03.dto.ShippingRequest;

public class OrchestrationUtil {

    public static void buildRequestContext(OrchestrationRequestContext ctx) {
        buildPaymentRequest(ctx);
        buildInventoryRequest(ctx);
        buildShippingRequest(ctx);
    }

    private static void buildPaymentRequest(OrchestrationRequestContext ctx) {
        var paymentRequest = PaymentRequest.builder()
                .orderId(ctx.getOrderId())
                .amount(ctx.getProductPrice() * ctx.getOrderRequest().getQuantity())
                .userId(ctx.getOrderRequest().getUserId())
                .build();
        ctx.setPaymentRequest(paymentRequest);
    }

    private static void buildInventoryRequest(OrchestrationRequestContext ctx) {
        InventoryRequest inventoryRequest = InventoryRequest.builder()
                .orderId(ctx.getOrderId())
                .quantity(ctx.getOrderRequest().getQuantity())
                .productId(ctx.getOrderRequest().getProductId())
                .build();
        ctx.setInventoryRequest(inventoryRequest);
    }

    private static void buildShippingRequest(OrchestrationRequestContext ctx) {
        ShippingRequest shippingRequest = ShippingRequest.builder()
                .orderId(ctx.getOrderId())
                .quantity(ctx.getOrderRequest().getQuantity())
                .userId(ctx.getOrderRequest().getUserId())
                .build();
        ctx.setShippingRequest(shippingRequest);
    }
}
