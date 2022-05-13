package net.shyshkin.study.webfluxpatterns.sec09.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.sec07.dto.ProductAggregate;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("sec09")
class ProductAggregateControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Order(10)
    @RepeatedTest(3)
    void getProductAggregate_ok() {
        //given
        Integer productId = 1;

        //when
        webTestClient.get()
                .uri("/sec09/product/{id}", productId)
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

    @Order(20)
    @RepeatedTest(3)
    void getProductAggregate_rateLimit() {
        //given
        Integer productId = 1;

        //when
        webTestClient.get()
                .uri("/sec09/product/{id}", productId)
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