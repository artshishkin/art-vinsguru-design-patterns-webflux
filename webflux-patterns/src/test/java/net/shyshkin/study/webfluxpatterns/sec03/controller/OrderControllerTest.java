package net.shyshkin.study.webfluxpatterns.sec03.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec03.client.InventoryClient;
import net.shyshkin.study.webfluxpatterns.sec03.client.ProductClient;
import net.shyshkin.study.webfluxpatterns.sec03.client.UserClient;
import net.shyshkin.study.webfluxpatterns.sec03.dto.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("sec03")
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

        OrderRequest orderRequest = OrderRequest.builder()
                .productId(productId)
                .userId(userId)
                .quantity(quantity)
                .build();

        //when
        webClient.post()
                .uri("/sec03/order")
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
    }

    @Test
    void placeOrder_absentUser() {
        //given
        int productId = 1;
        int userId = 51;
        int quantity = 1;

        OrderRequest orderRequest = OrderRequest.builder()
                .productId(productId)
                .userId(userId)
                .quantity(quantity)
                .build();

        //when
        webClient.post()
                .uri("/sec03/order")
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
    }

    @Test
    @Disabled("Bug - HttpResponse 500")
    void placeOrder_absentProduct() {
        //given
        int productId = 51;
        int userId = 1;
        int quantity = 1;

        OrderRequest orderRequest = OrderRequest.builder()
                .productId(productId)
                .userId(userId)
                .quantity(quantity)
                .build();

        //when
        webClient.post()
                .uri("/sec03/order")
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

        OrderRequest orderRequest = OrderRequest.builder()
                .productId(productId)
                .userId(userId)
                .quantity(quantity)
                .build();

        //when
        webClient.post()
                .uri("/sec03/order")
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
    }

    private Mono<User> getUserWithMinBalance() {
        return Flux.range(1, 50)
                .flatMap(userClient::getUser)
                .reduce((u1, u2) -> u1.getBalance() < u2.getBalance() ? u1 : u2);
    }

    private Mono<Product> getProductWithMaxPrice() {
        return Flux.range(1, 50)
                .flatMap(productClient::getProduct)
                .reduce((p1, p2) -> p1.getPrice() > p2.getPrice() ? p1 : p2);
    }

    @Test
    void placeOrder_notEnoughQuantity() {
        //given
        int productId = 1;
        int userId = 1;
        int quantity = 1000;

        OrderRequest orderRequest = OrderRequest.builder()
                .productId(productId)
                .userId(userId)
                .quantity(quantity)
                .build();

        //when
        webClient.post()
                .uri("/sec03/order")
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
    }

    @RepeatedTest(10)
    @DisplayName("Shipping Service randomly drops an error so search debug logs for FAILED status")
    void placeOrder_shippingFailure() {
        //given
        int productId = 4;
        int userId = 3;
        int quantity = 0;

        OrderRequest orderRequest = OrderRequest.builder()
                .productId(productId)
                .userId(userId)
                .quantity(quantity)
                .build();

        //when
        webClient.post()
                .uri("/sec03/order")
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
    }
}