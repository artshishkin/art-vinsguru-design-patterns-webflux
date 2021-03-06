package net.shyshkin.study.webfluxpatterns.sec01.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec01.dto.ProductAggregate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("sec01")
class ProductAggregateControllerTest extends ExternalServiceAbstractTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    @DisplayName("When every service returns correct data aggregator service should respond correctly")
    void getProductAggregate_ok() {
        //given
        Integer productId = 1;

        //when
        webTestClient.get()
                .uri("/sec01/product/{id}", productId)
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
                                        .allSatisfy(review -> assertThat(review).hasNoNullFieldsOrProperties())
                        )
                )
                .value(aggregate -> log.debug("{}", aggregate));
    }

    @Test
    @DisplayName("When product service returns error aggregator service should return service should respond 404")
    void getProductAggregate_404() {
        //given
        Integer productId = 51;

        //when
        webTestClient.get()
                .uri("/sec01/product/{id}", productId)
                .exchange()

                //then
                .expectStatus().isNotFound();
    }

    @ParameterizedTest
    @DisplayName("When one of services respond with 404 or 500 Status code we got 500 from Aggregation controller")
    @ValueSource(ints = {5, 7})
    @Disabled("Resilience fixed")
    void getProductAggregate_500Error(int productId) {

        //when
        webTestClient.get()
                .uri("/sec01/product/{id}", productId)
                .exchange()

                //then
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .value(aggregate -> log.debug("Error: {}", aggregate));
    }

    @ParameterizedTest
    @DisplayName("When promotion or review services respond with 404 or 500 Status code we should get 200 from Aggregation controller")
    @MethodSource("productIdStream")
    void getProductAggregate_200_OK_whenExternalServiceFails(int productId) {

        //when
        webTestClient.get()
                .uri("/sec01/product/{id}", productId)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(ProductAggregate.class)
                .value(aggregate -> assertThat(aggregate)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("id", productId))
                .value(aggregate -> log.debug("{}", aggregate));
    }

    static IntStream productIdStream() {
        return IntStream.rangeClosed(1, 50);
    }

}