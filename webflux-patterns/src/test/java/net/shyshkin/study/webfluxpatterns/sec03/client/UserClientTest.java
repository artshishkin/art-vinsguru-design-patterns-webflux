package net.shyshkin.study.webfluxpatterns.sec03.client;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec03.dto.PaymentRequest;
import net.shyshkin.study.webfluxpatterns.sec03.dto.Status;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@Disabled
@ActiveProfiles("sec03")
class UserClientTest extends ExternalServiceAbstractTest {

    @Autowired
    UserClient client;

    @Test
    void getUser_ok() {
        //given
        Integer userId = 1;

        //when
        StepVerifier.create(client.getUser(userId))

                //then
                .consumeNextWith(user -> assertAll(
                        () -> assertThat(user).hasNoNullFieldsOrProperties(),
                        () -> assertThat(user.getUserId()).isEqualTo(userId)
                ))
                .verifyComplete();
    }

    @Test
    void getUser_absent() {
        //given
        Integer userId = 51;

        //when
        StepVerifier.create(client.getUser(userId))

                //then
                .verifyComplete();
    }

    @Test
    void deduct_ok() {
        //given
        Integer userId = 2;
//        UUID orderId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
        UUID orderId = UUID.randomUUID();
        Integer amount = 1;
        var paymentRequest = PaymentRequest.builder()
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .build();
        //when
        StepVerifier.create(client.deduct(paymentRequest))

                //then
                .consumeNextWith(paymentResponse -> assertAll(
                        () -> assertThat(paymentResponse).hasNoNullFieldsOrProperties(),
                        () -> assertThat(paymentResponse.getUserId()).isEqualTo(userId),
                        () -> assertThat(paymentResponse.getStatus()).isEqualTo(Status.SUCCESS),
                        () -> log.debug("Payment Response: {}", paymentResponse)
                ))
                .verifyComplete();
    }

    @Test
    void deduct_absentUser() {
        //given
        Integer userId = 51;
        UUID orderId = UUID.randomUUID();
        Integer amount = 1;
        var paymentRequest = PaymentRequest.builder()
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .build();
        //when
        StepVerifier.create(client.deduct(paymentRequest))

                //then
                .verifyErrorSatisfies(error -> assertThat(error)
                        .isInstanceOf(WebClientResponseException.class)
                        .hasMessageContaining("404 Not Found"));
    }

    @Test
    void deduct_notEnoughMoney() {
        //given
        Integer userId = 2;
        UUID orderId = UUID.randomUUID();
        Integer amount = 1000_000;
        var paymentRequest = PaymentRequest.builder()
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .build();
        //when
        StepVerifier.create(client.deduct(paymentRequest))

                //then
                .verifyErrorSatisfies(error -> {
                    error.printStackTrace();
                    assertThat(error)
                            .isInstanceOf(WebClientResponseException.class)
                            .hasMessageContaining("500 Internal Server Error");
                });
    }

    @Test
    void refund_ok() {
        //given
        Integer userId = 2;
        UUID orderId = UUID.randomUUID();
        Integer amount = 1;
        var paymentRequest = PaymentRequest.builder()
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .build();
        //when
        StepVerifier.create(client.refund(paymentRequest))

                //then
                .consumeNextWith(paymentResponse -> assertAll(
                        () -> assertThat(paymentResponse).hasNoNullFieldsOrProperties(),
                        () -> assertThat(paymentResponse.getUserId()).isEqualTo(userId),
                        () -> assertThat(paymentResponse.getStatus()).isEqualTo(Status.SUCCESS),
                        () -> log.debug("Payment Response: {}", paymentResponse)
                ))
                .verifyComplete();
    }


}