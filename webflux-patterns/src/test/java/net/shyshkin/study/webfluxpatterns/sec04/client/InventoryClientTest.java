package net.shyshkin.study.webfluxpatterns.sec04.client;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec04.dto.InventoryRequest;
import net.shyshkin.study.webfluxpatterns.sec04.dto.PaymentRequest;
import net.shyshkin.study.webfluxpatterns.sec04.dto.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@DisplayName("Section04 InventoryClientTest")
@ActiveProfiles("sec04")
class InventoryClientTest extends ExternalServiceAbstractTest {

    @Autowired
    InventoryClient client;

    @Autowired
    UserClient userClient;

    @Nested
    class GetInventoryTests {

        @Test
        void getInventory_ok() {
            //given
            Integer productId = 1;

            //when
            StepVerifier.create(client.getInventory(productId))

                    //then
                    .consumeNextWith(inventory -> assertAll(
                            () -> assertThat(inventory).isGreaterThan(0)
                    ))
                    .verifyComplete();
        }

        @Test
        void getInventory_absent() {
            //given
            Integer productId = 51;

            //when
            StepVerifier.create(client.getInventory(productId))

                    //then
                    .verifyComplete();
        }

    }

    @Nested
    class DeductTests {

        UUID paymentId;

        @BeforeEach
        void setUp() {

            Integer userId = 2;
            UUID orderId = UUID.randomUUID();
            Integer amount = 1;
            var paymentRequest = PaymentRequest.builder()
                    .orderId(orderId)
                    .userId(userId)
                    .amount(amount)
                    .build();

            var paymentResponse = userClient.deduct(paymentRequest).block();
            paymentId =  paymentResponse.getPaymentId();
        }

        @Test
        void deduct_ok() {
            //given
            Integer productId = 2;
            Integer quantity = 1;
            var inventoryRequest = InventoryRequest.builder()
                    .paymentId(paymentId)
                    .productId(productId)
                    .quantity(quantity)
                    .build();
            //when
            StepVerifier.create(client.deduct(inventoryRequest))

                    //then
                    .consumeNextWith(inventoryResponse -> assertAll(
                            () -> assertThat(inventoryResponse).hasNoNullFieldsOrProperties(),
                            () -> assertThat(inventoryResponse.getProductId()).isEqualTo(productId),
                            () -> assertThat(inventoryResponse.getStatus()).isEqualTo(Status.SUCCESS),
                            () -> log.debug("Inventory Response: {}", inventoryResponse)
                    ))
                    .verifyComplete();
        }

        @Test
        void deduct_absentProduct() {
            //given
            Integer productId = 51;
            Integer quantity = 1;
            var inventoryRequest = InventoryRequest.builder()
                    .paymentId(paymentId)
                    .productId(productId)
                    .quantity(quantity)
                    .build();
            //when
            StepVerifier.create(client.deduct(inventoryRequest))

                    //then
                    .consumeNextWith(inventoryResponse -> assertAll(
                            () -> assertThat(inventoryResponse.getProductId()).isEqualTo(productId),
                            () -> assertThat(inventoryResponse.getStatus()).isEqualTo(Status.FAILED),
                            () -> assertThat(inventoryResponse.getQuantity()).isEqualTo(quantity),
                            () -> assertThat(inventoryResponse.getRemainingQuantity()).isNull(),
                            () -> log.debug("Inventory Response: {}", inventoryResponse)
                    ))
                    .verifyComplete();
        }

        @Test
        void deduct_notEnoughQuantity() {
            //given
            Integer productId = 2;
            Integer quantity = 1000_000;
            var inventoryRequest = InventoryRequest.builder()
                    .paymentId(paymentId)
                    .productId(productId)
                    .quantity(quantity)
                    .build();
            //when
            StepVerifier.create(client.deduct(inventoryRequest))

                    //then
                    .consumeNextWith(inventoryResponse -> assertAll(
                            () -> assertThat(inventoryResponse.getProductId()).isEqualTo(productId),
                            () -> assertThat(inventoryResponse.getStatus()).isEqualTo(Status.FAILED),
                            () -> assertThat(inventoryResponse.getQuantity()).isEqualTo(quantity),
                            () -> assertThat(inventoryResponse.getRemainingQuantity()).isNotEqualTo(0),
                            () -> log.debug("Inventory Response: {}", inventoryResponse)
                    ))
                    .verifyComplete();
        }
    }

    @Nested
    class RestoreTests {

        UUID paymentId;

        @BeforeEach
        void setUp() {

            Integer userId = 2;
            UUID orderId = UUID.randomUUID();
            Integer amount = 1;
            var paymentRequest = PaymentRequest.builder()
                    .orderId(orderId)
                    .userId(userId)
                    .amount(amount)
                    .build();

            var paymentResponse = userClient.deduct(paymentRequest).block();
            paymentId =  paymentResponse.getPaymentId();
        }

        @Test
        void restore_ok() {
            //given
            Integer productId = 3;
            Integer quantity = 1;
            var inventoryRequest = InventoryRequest.builder()
                    .paymentId(paymentId)
                    .productId(productId)
                    .quantity(quantity)
                    .build();
            //when
            StepVerifier.create(client.restore(inventoryRequest))

                    //then
                    .consumeNextWith(inventoryResponse -> assertAll(
                            () -> assertThat(inventoryResponse).hasNoNullFieldsOrPropertiesExcept("inventoryId"),
                            () -> assertThat(inventoryResponse.getInventoryId()).isNull(),
                            () -> assertThat(inventoryResponse.getProductId()).isEqualTo(productId),
                            () -> assertThat(inventoryResponse.getStatus()).isEqualTo(Status.SUCCESS),
                            () -> log.debug("Inventory Response: {}", inventoryResponse)
                    ))
                    .verifyComplete();
        }
    }
}