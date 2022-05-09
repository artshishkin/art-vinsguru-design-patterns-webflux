package net.shyshkin.study.webfluxpatterns.sec04.controller;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec04.client.InventoryClient;
import net.shyshkin.study.webfluxpatterns.sec04.client.ProductClient;
import net.shyshkin.study.webfluxpatterns.sec04.client.UserClient;
import net.shyshkin.study.webfluxpatterns.sec04.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.math.MathFlux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Section04 OrderControllerTest")
@ActiveProfiles("sec04")
class OrderControllerTest extends ExternalServiceAbstractTest {

    @Autowired
    WebTestClient webClient;

    @Autowired
    UserClient userClient;

    @Autowired
    ProductClient productClient;

    @Autowired
    InventoryClient inventoryClient;

    @Test
    void placeOrder_ok() {
        //given
        int productId = 1;
        int userId = 1;
        int quantity = 1;

        var initialState = new ServicesState(productId, userId).stateMono.block();

        OrderRequest orderRequest = OrderRequest.builder()
                .productId(productId)
                .userId(userId)
                .quantity(quantity)
                .build();

        //when
        webClient.post()
                .uri("/sec04/order")
                .bodyValue(orderRequest)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(OrderResponse.class)
                .value(orderResponse -> assertAll(
                        () -> assertThat(orderResponse).hasNoNullFieldsOrProperties(),
                        () -> assertThat(orderResponse.getAddress()).hasNoNullFieldsOrProperties(),
                        () -> assertThat(orderResponse.getProductId()).isEqualTo(productId),
                        () -> assertThat(orderResponse.getUserId()).isEqualTo(userId),
                        () -> assertThat(orderResponse.getStatus()).isEqualTo(Status.SUCCESS),
                        () -> assertThat(orderResponse.getExpectedDelivery()).isAfter(LocalDate.now()),
                        () -> log.debug("Order Response: {}", orderResponse)
                ));

        StepVerifier.create(new ServicesState(productId, userId).stateMono)
                .consumeNextWith(finalState -> assertAll(
                        () -> log.debug("Initial State: {}. Final State: {}", initialState, finalState),
                        () -> assertThat(finalState.inventoryQuantity).isEqualTo(initialState.inventoryQuantity - quantity),
                        () -> assertThat(finalState.userBalance).isEqualTo(initialState.userBalance - quantity * finalState.productPrice)
                ))
                .verifyComplete();
    }

    @Test
    void placeOrder_absentUser() {
        //given
        int productId = 1;
        int userId = 51;
        int quantity = 1;

        var initialState = new ServicesState(productId, userId).stateMono.block();

        OrderRequest orderRequest = OrderRequest.builder()
                .productId(productId)
                .userId(userId)
                .quantity(quantity)
                .build();

        //when
        webClient.post()
                .uri("/sec04/order")
                .bodyValue(orderRequest)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(OrderResponse.class)
                .value(orderResponse -> assertAll(
                        () -> assertThat(orderResponse.getOrderId()).isNotNull(),
                        () -> assertThat(orderResponse.getProductId()).isEqualTo(productId),
                        () -> assertThat(orderResponse.getUserId()).isEqualTo(userId),
                        () -> assertThat(orderResponse.getAddress()).isNull(),
                        () -> assertThat(orderResponse.getExpectedDelivery()).isNull(),
                        () -> assertThat(orderResponse.getStatus()).isEqualTo(Status.FAILED),
                        () -> log.debug("Order Response: {}", orderResponse)
                ));

        var finalState = new ServicesState(productId, userId).stateMono.block();
        log.debug("Initial State: {}. Final State: {}", initialState, finalState);
        assertThat(finalState).isEqualTo(initialState);

    }

    @Test
    void placeOrder_absentProduct() {
        //given
        int productId = 51;
        int userId = 1;
        int quantity = 1;

        var initialState = new ServicesState(productId, userId).stateMono.block();

        OrderRequest orderRequest = OrderRequest.builder()
                .productId(productId)
                .userId(userId)
                .quantity(quantity)
                .build();

        //when
        webClient.post()
                .uri("/sec04/order")
                .bodyValue(orderRequest)
                .exchange()

                //then
                .expectStatus().isNotFound()
                .expectBody().isEmpty();

        var finalState = new ServicesState(productId, userId).stateMono.block();
        log.debug("Initial State: {}. Final State: {}", initialState, finalState);
        assertThat(finalState).isEqualTo(initialState);

    }

    @Test
    void placeOrder_notEnoughMoney() {
        //given
        int productId = getProductWithMaxPrice()
                .map(Product::getId)
                .block();
        int userId = getUserWithMinBalance()
                .map(User::getUserId)
                .block();
        int quantity = 9;

        var initialState = new ServicesState(productId, userId).stateMono.block();

        OrderRequest orderRequest = OrderRequest.builder()
                .productId(productId)
                .userId(userId)
                .quantity(quantity)
                .build();

        //when
        webClient.post()
                .uri("/sec04/order")
                .bodyValue(orderRequest)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(OrderResponse.class)
                .value(orderResponse -> assertAll(
                        () -> assertThat(orderResponse.getOrderId()).isNotNull(),
                        () -> assertThat(orderResponse.getProductId()).isEqualTo(productId),
                        () -> assertThat(orderResponse.getUserId()).isEqualTo(userId),
                        () -> assertThat(orderResponse.getAddress()).isNull(),
                        () -> assertThat(orderResponse.getExpectedDelivery()).isNull(),
                        () -> assertThat(orderResponse.getStatus()).isEqualTo(Status.FAILED),
                        () -> log.debug("Order Response: {}", orderResponse)
                ));

        var finalState = new ServicesState(productId, userId).stateMono.block();
        log.debug("Initial State: {}. Final State: {}", initialState, finalState);
        assertThat(finalState).isEqualTo(initialState);

    }

    private Mono<User> getUserWithMinBalance() {
        return MathFlux.max(
                Flux.range(1, 50).flatMap(userClient::getUser),
                Comparator.comparing(User::getBalance)
        );
    }

    private Mono<Product> getProductWithMaxPrice() {
        return MathFlux.max(
                Flux.range(1, 50).flatMap(productClient::getProduct),
                Comparator.comparing(Product::getPrice)
        );
    }

    @Test
    void placeOrder_notEnoughQuantity() {
        //given
        int productId = 1;
        int userId = 1;
        int quantity = 1000;

        var initialState = new ServicesState(productId, userId).stateMono.block();

        OrderRequest orderRequest = OrderRequest.builder()
                .productId(productId)
                .userId(userId)
                .quantity(quantity)
                .build();

        //when
        webClient.post()
                .uri("/sec04/order")
                .bodyValue(orderRequest)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(OrderResponse.class)
                .value(orderResponse -> assertAll(
                        () -> assertThat(orderResponse.getOrderId()).isNotNull(),
                        () -> assertThat(orderResponse.getProductId()).isEqualTo(productId),
                        () -> assertThat(orderResponse.getUserId()).isEqualTo(userId),
                        () -> assertThat(orderResponse.getAddress()).isNull(),
                        () -> assertThat(orderResponse.getExpectedDelivery()).isNull(),
                        () -> assertThat(orderResponse.getStatus()).isEqualTo(Status.FAILED),
                        () -> log.debug("Order Response: {}", orderResponse)
                ));

        var finalState = new ServicesState(productId, userId).stateMono.block();
        log.debug("Initial State: {}. Final State: {}", initialState, finalState);
        assertThat(finalState).isEqualTo(initialState);

    }

    @RepeatedTest(10)
    @DisplayName("Shipping Service randomly drops an error so search debug logs for FAILED status")
    void placeOrder_shippingFailure() {
        //given
        int productId = 4;
        int userId = 3;
        int quantity = 0;

        var initialState = new ServicesState(productId, userId).stateMono.block();

        OrderRequest orderRequest = OrderRequest.builder()
                .productId(productId)
                .userId(userId)
                .quantity(quantity)
                .build();

        //when
        webClient.post()
                .uri("/sec04/order")
                .bodyValue(orderRequest)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(OrderResponse.class)
                .value(orderResponse -> assertAll(
                        () -> assertThat(orderResponse.getOrderId()).isNotNull(),
                        () -> assertThat(orderResponse.getProductId()).isEqualTo(productId),
                        () -> assertThat(orderResponse.getUserId()).isEqualTo(userId),
                        () -> {
                            if (Status.FAILED.equals(orderResponse.getStatus()))
                                log.debug("Order Response: {}", orderResponse);
                        }
                ));

        var finalState = new ServicesState(productId, userId).stateMono.block();
        log.debug("Initial State: {}. Final State: {}", initialState, finalState);
        assertThat(finalState).isEqualTo(initialState);

    }

    @Getter
    @EqualsAndHashCode
    @ToString
    private class ServicesState {

        private Integer productPrice;
        private Integer inventoryQuantity;
        private Integer userBalance;

        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        private final Mono<ServicesState> stateMono;

        public ServicesState(Integer productId, Integer userId) {

            this.stateMono = Mono.zip(
                            productClient.getProduct(productId),
                            inventoryClient.getInventory(productId),
                            userClient.getUser(userId)
                    )
                    .doOnNext(tuple -> {
                        productPrice = tuple.getT1().getPrice();
                        inventoryQuantity = tuple.getT2();
                        userBalance = tuple.getT3().getBalance();
                    })
                    .thenReturn(this);
        }
    }
}