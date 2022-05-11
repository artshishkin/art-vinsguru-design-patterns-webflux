package net.shyshkin.study.webfluxpatterns.sec07.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec07.dto.ProductAggregate;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("sec07")
class ProductAggregateControllerTest extends ExternalServiceAbstractTest {

    @Autowired
    WebTestClient webTestClient;

    @Order(10)
    @Test
    @DisplayName("Warming Up JVM")
    void getProductAggregate_warmUp() {
        //given
        Integer productId = 1;

        //when
        webTestClient.get()
                .uri("/sec07/product/{id}", productId)
                .exchange()

                //then
                .expectStatus().isOk();
    }

    @Order(20)
    @RepeatedTest(10)
    @DisplayName("All requests should return OK")
    void getProductAggregate_ok() {
        //given
        Integer productId = 1;

        //when
        webTestClient.get()
                .uri("/sec07/product/{id}", productId)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(ProductAggregate.class)
                .value(aggregate -> assertAll(
                        () -> assertThat(aggregate)
                                .hasNoNullFieldsOrProperties()
                                .hasFieldOrPropertyWithValue("id", productId),
                        () -> assertThat(aggregate.getReviews())
                                .hasSizeGreaterThanOrEqualTo(1)
                                .allSatisfy(review -> assertThat(review).hasNoNullFieldsOrProperties()),
                        () -> log.debug("Aggregate: {}", aggregate)
                ));
    }

    @Order(30)
    @Test
    @DisplayName("Requests with id of product without reviews should return empty list without Retry")
    void getProductAggregate_absentReview() {
        //given
        Integer productId = 10;

        //when
        webTestClient.get()
                .uri("/sec07/product/{id}", productId)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(ProductAggregate.class)
                .value(aggregate -> assertAll(
                        () -> assertThat(aggregate)
                                .hasNoNullFieldsOrProperties()
                                .hasFieldOrPropertyWithValue("id", productId),
                        () -> assertThat(aggregate.getReviews())
                                .hasSize(0),
                        () -> log.debug("Aggregate: {}", aggregate)
                ));
    }

}