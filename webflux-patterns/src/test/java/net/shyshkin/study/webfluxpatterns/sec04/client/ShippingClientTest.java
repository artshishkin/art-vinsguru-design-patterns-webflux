package net.shyshkin.study.webfluxpatterns.sec04.client;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec04.dto.InventoryRequest;
import net.shyshkin.study.webfluxpatterns.sec04.dto.InventoryResponse;
import net.shyshkin.study.webfluxpatterns.sec04.dto.PaymentRequest;
import net.shyshkin.study.webfluxpatterns.sec04.dto.ShippingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@ActiveProfiles("sec04")
class ShippingClientTest extends ExternalServiceAbstractTest {

    @Autowired
    ShippingClient client;

    @Autowired
    UserClient userClient;

    @Autowired
    InventoryClient inventoryClient;

    @Nested
    class ScheduleTests {

        UUID inventoryId;

        Integer quantity = 1;
        Integer userId;

        @BeforeEach
        void setUp() {

            userId = ThreadLocalRandom.current().nextInt(1, 50);
            UUID orderId = UUID.randomUUID();
            Integer amount = 1;
            Integer productId = ThreadLocalRandom.current().nextInt(1, 50);

            var paymentRequest = PaymentRequest.builder()
                    .orderId(orderId)
                    .userId(userId)
                    .amount(amount)
                    .build();

            var paymentResponse = userClient.deduct(paymentRequest).block();
            var paymentId = paymentResponse.getPaymentId();

            var inventoryRequest = InventoryRequest.builder()
                    .paymentId(paymentId)
                    .productId(productId)
                    .quantity(quantity)
                    .build();
            InventoryResponse inventoryResponse = inventoryClient.deduct(inventoryRequest).block();
            inventoryId = inventoryResponse.getInventoryId();

        }

        @RepeatedTest(20)
        void schedule() {
            //given
            var shippingRequest = ShippingRequest.builder()
                    .inventoryId(inventoryId)
                    .userId(userId)
                    .quantity(quantity)
                    .build();
            //when
            StepVerifier.create(client.schedule(shippingRequest))

                    //then
                    .consumeNextWith(shippingResponse -> assertAll(
                            () -> assertThat(shippingResponse.getQuantity()).isEqualTo(quantity),
                            () -> assertThat(shippingResponse).hasFieldOrProperty("status"),
                            () -> log.debug("Shipping Response: {}", shippingResponse)
                    ))
                    .verifyComplete();
        }
    }

    @Test
    void cancel() {
        //given
        Integer userId = 3;
        UUID inventoryId = UUID.randomUUID();
        Integer quantity = 1;
        var shippingRequest = ShippingRequest.builder()
                .inventoryId(inventoryId)
                .userId(userId)
                .quantity(quantity)
                .build();
        //when
        StepVerifier.create(client.cancel(shippingRequest))

                //then
                .verifyComplete();
    }

}