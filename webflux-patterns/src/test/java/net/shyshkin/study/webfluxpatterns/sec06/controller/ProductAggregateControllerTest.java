package net.shyshkin.study.webfluxpatterns.sec06.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec06.dto.ProductAggregate;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("sec06")
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
                .uri("/sec06/product/{id}", productId)
                .exchange()

                //then
                .expectStatus().isOk();
    }

    @Order(20)
    @RepeatedTest(10)
    @DisplayName("Request should take less then 560 seconds")
    void getProductAggregate_expectDuration() {
        //given
        Integer productId = 1;
        LocalDateTime startTest = LocalDateTime.now();
        Duration maxExpectedDuration = Duration.ofMillis(560);

        //when
        Flux<ProductAggregate> responseBody = webTestClient.get()
                .uri("/sec06/product/{id}", productId)
                .exchange()

                //then
                .expectStatus().isOk()
                .returnResult(ProductAggregate.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .consumeNextWith(aggregate -> assertAll(
                        () -> assertThat(aggregate)
                                .hasNoNullFieldsOrProperties()
                                .hasFieldOrPropertyWithValue("id", productId),
                        () -> assertThat(aggregate.getReviews())
                                .allSatisfy(review -> assertThat(review).hasNoNullFieldsOrProperties()),
                        () -> log.debug("Aggregate: {}", aggregate)
                ))
                .verifyComplete();
        LocalDateTime finishTest = LocalDateTime.now();
        Duration testDuration = Duration.between(startTest, finishTest);

        assertThat(testDuration).isBetween(Duration.ofMillis(10), maxExpectedDuration);
    }

}