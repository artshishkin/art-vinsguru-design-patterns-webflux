package net.shyshkin.study.webfluxpatterns.sec03.client;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec03.dto.ShippingRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@DisplayName("Section03 ShippingClientTest")
@ActiveProfiles("sec03")
class ShippingClientTest extends ExternalServiceAbstractTest {

    @Autowired
    ShippingClient client;

    @RepeatedTest(20)
    void schedule() {
        //given
        Integer userId = 2;
        UUID orderId = UUID.randomUUID();
        Integer quantity = 1;
        var shippingRequest = ShippingRequest.builder()
                .orderId(orderId)
                .userId(userId)
                .quantity(quantity)
                .build();
        //when
        StepVerifier.create(client.schedule(shippingRequest))

                //then
                .consumeNextWith(shippingResponse -> assertAll(
                        () -> assertThat(shippingResponse.getOrderId()).isEqualTo(orderId),
                        () -> assertThat(shippingResponse.getQuantity()).isEqualTo(quantity),
                        () -> assertThat(shippingResponse).hasFieldOrProperty("status"),
                        () -> log.debug("Shipping Response: {}", shippingResponse)
                ))
                .verifyComplete();
    }

    @Test
    void cancel() {
        //given
        Integer userId = 3;
        UUID orderId = UUID.randomUUID();
        Integer quantity = 1;
        var shippingRequest = ShippingRequest.builder()
                .orderId(orderId)
                .userId(userId)
                .quantity(quantity)
                .build();
        //when
        StepVerifier.create(client.cancel(shippingRequest))

                //then
                .verifyComplete();
    }

}