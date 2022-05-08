package net.shyshkin.study.webfluxpatterns.sec03.client;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec03.dto.InventoryRequest;
import net.shyshkin.study.webfluxpatterns.sec03.dto.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@DisplayName("Section03 InventoryClientTest")
@ActiveProfiles("sec03")
class InventoryClientTest extends ExternalServiceAbstractTest {

    @Autowired
    InventoryClient client;

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

    @Test
    void deduct_ok() {
        //given
        Integer productId = 2;
        UUID orderId = UUID.randomUUID();
        Integer quantity = 1;
        var inventoryRequest = InventoryRequest.builder()
                .orderId(orderId)
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
        UUID orderId = UUID.randomUUID();
        Integer quantity = 1;
        var inventoryRequest = InventoryRequest.builder()
                .orderId(orderId)
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
        UUID orderId = UUID.randomUUID();
        Integer quantity = 1000_000;
        var inventoryRequest = InventoryRequest.builder()
                .orderId(orderId)
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

    @Test
    void restore_ok() {
        //given
        Integer productId = 3;
        UUID orderId = UUID.randomUUID();
        Integer quantity = 1;
        var inventoryRequest = InventoryRequest.builder()
                .orderId(orderId)
                .productId(productId)
                .quantity(quantity)
                .build();
        //when
        StepVerifier.create(client.restore(inventoryRequest))

                //then
                .consumeNextWith(inventoryResponse -> assertAll(
                        () -> assertThat(inventoryResponse).hasNoNullFieldsOrProperties(),
                        () -> assertThat(inventoryResponse.getProductId()).isEqualTo(productId),
                        () -> assertThat(inventoryResponse.getStatus()).isEqualTo(Status.SUCCESS),
                        () -> log.debug("Inventory Response: {}", inventoryResponse)
                ))
                .verifyComplete();
    }

}