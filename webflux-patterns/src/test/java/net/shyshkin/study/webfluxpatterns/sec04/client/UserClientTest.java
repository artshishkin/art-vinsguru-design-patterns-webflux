package net.shyshkin.study.webfluxpatterns.sec04.client;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec04.dto.PaymentRequest;
import net.shyshkin.study.webfluxpatterns.sec04.dto.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@DisplayName("Section04 UserClientTest")
@ActiveProfiles("sec04")
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
                .consumeNextWith(paymentResponse -> assertAll(
                        () -> assertThat(paymentResponse.getName()).isNull(),
                        () -> assertThat(paymentResponse.getUserId()).isEqualTo(userId),
                        () -> assertThat(paymentResponse.getStatus()).isEqualTo(Status.FAILED),
                        () -> assertThat(paymentResponse.getBalance()).isEqualTo(amount),
                        () -> log.debug("Payment Response: {}", paymentResponse)
                ))
                .verifyComplete();
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
                .consumeNextWith(paymentResponse -> assertAll(
                        () -> assertThat(paymentResponse.getName()).isNotEmpty(),
                        () -> assertThat(paymentResponse.getUserId()).isEqualTo(userId),
                        () -> assertThat(paymentResponse.getStatus()).isEqualTo(Status.FAILED),
                        () -> assertThat(paymentResponse.getBalance()).isNotEqualTo(amount),
                        () -> log.debug("Payment Response: {}", paymentResponse)
                ))
                .verifyComplete();
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
                        () -> assertThat(paymentResponse).hasNoNullFieldsOrPropertiesExcept("paymentId"),
                        () -> assertThat(paymentResponse.getUserId()).isEqualTo(userId),
                        () -> assertThat(paymentResponse.getStatus()).isEqualTo(Status.SUCCESS),
                        () -> log.debug("Payment Response: {}", paymentResponse)
                ))
                .verifyComplete();
    }


}