package net.shyshkin.study.webfluxpatterns.sec04.util;

import net.shyshkin.study.webfluxpatterns.sec04.dto.InventoryRequest;
import net.shyshkin.study.webfluxpatterns.sec04.dto.OrchestrationRequestContext;
import net.shyshkin.study.webfluxpatterns.sec04.dto.PaymentRequest;
import net.shyshkin.study.webfluxpatterns.sec04.dto.ShippingRequest;

public class OrchestrationUtil {

    public static void buildPaymentRequest(OrchestrationRequestContext ctx) {
        var paymentRequest = PaymentRequest.builder()
                .orderId(ctx.getOrderId())
                .amount(ctx.getProductPrice() * ctx.getOrderRequest().getQuantity())
                .userId(ctx.getOrderRequest().getUserId())
                .build();
        ctx.setPaymentRequest(paymentRequest);
    }

    public static void buildInventoryRequest(OrchestrationRequestContext ctx) {
        InventoryRequest inventoryRequest = InventoryRequest.builder()
                .paymentId(ctx.getPaymentResponse().getPaymentId())
                .quantity(ctx.getOrderRequest().getQuantity())
                .productId(ctx.getOrderRequest().getProductId())
                .build();
        ctx.setInventoryRequest(inventoryRequest);
    }

    public static void buildShippingRequest(OrchestrationRequestContext ctx) {
        ShippingRequest shippingRequest = ShippingRequest.builder()
                .inventoryId(ctx.getInventoryResponse().getInventoryId())
                .quantity(ctx.getOrderRequest().getQuantity())
                .userId(ctx.getOrderRequest().getUserId())
                .build();
        ctx.setShippingRequest(shippingRequest);
    }
}
