package net.shyshkin.study.webfluxpatterns.sec04.service;

import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec04.dto.OrchestrationRequestContext;
import net.shyshkin.study.webfluxpatterns.sec04.dto.OrderRequest;
import net.shyshkin.study.webfluxpatterns.sec04.dto.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("sec04")
class OrderFulfillmentServiceTest extends ExternalServiceAbstractTest {

    @Autowired
    OrderFulfillmentService orderFulfillmentService;

    @Test
    void placeOrder_ok() {
        //given
        int productId = 13;
        int quantity = 1;
        int userId = 13;

        OrderRequest orderRequest = OrderRequest.builder()
                .productId(productId)
                .quantity(quantity)
                .userId(userId)
                .build();

        OrchestrationRequestContext context = new OrchestrationRequestContext(orderRequest);

        //when
        StepVerifier.create(orderFulfillmentService.placeOrder(context))

                //then
                .consumeNextWith(ctx -> assertAll(
                        () -> assertThat(ctx).hasNoNullFieldsOrProperties(),
                        () -> assertThat(ctx.getStatus()).isEqualTo(Status.SUCCESS),
                        () -> assertThat(ctx.getShippingResponse().getQuantity()).isEqualTo(quantity),
                        () -> assertThat(ctx.getPaymentResponse().getUserId()).isEqualTo(userId),
                        () -> assertThat(ctx.getInventoryResponse().getProductId()).isEqualTo(productId)
                ))
                .verifyComplete();
    }

    @Test
    void placeOrder_absentProduct() {
        //given
        int productId = 51;
        int quantity = 1;
        int userId = 13;

        OrderRequest orderRequest = OrderRequest.builder()
                .productId(productId)
                .quantity(quantity)
                .userId(userId)
                .build();

        OrchestrationRequestContext context = new OrchestrationRequestContext(orderRequest);

        //when
        StepVerifier.create(orderFulfillmentService.placeOrder(context))

                //then
                .verifyComplete();
    }

    @Test
    void placeOrder_absentUser() {
        //given
        int productId = 1;
        int quantity = 1;
        int userId = 51;

        OrderRequest orderRequest = OrderRequest.builder()
                .productId(productId)
                .quantity(quantity)
                .userId(userId)
                .build();

        OrchestrationRequestContext context = new OrchestrationRequestContext(orderRequest);

        //when
        StepVerifier.create(orderFulfillmentService.placeOrder(context))

                //then
                .consumeNextWith(ctx -> assertAll(
                        () -> assertThat(ctx.getStatus()).isEqualTo(Status.FAILED),
                        () -> assertThat(ctx.getPaymentResponse().getStatus()).isEqualTo(Status.FAILED),
                        () -> assertThat(ctx.getInventoryRequest()).isNull(),
                        () -> assertThat(ctx.getInventoryResponse()).isNull(),
                        () -> assertThat(ctx.getShippingRequest()).isNull(),
                        () -> assertThat(ctx.getShippingResponse()).isNull()
                ))
                .verifyComplete();
    }

}