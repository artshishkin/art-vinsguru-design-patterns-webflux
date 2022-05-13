package net.shyshkin.study.webfluxpatterns.sec08.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.sec07.dto.ProductAggregate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@ActiveProfiles("sec08")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "logging.level.net.shyshkin=debug",
        "app.external.service.baseUrl=http://localhost:7075"
})
@Disabled("Only for manual testing. Run external services on port 7075 first. `docker-compose up`")
class ProductAggregateControllerManualTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void circuitBreakerTest() {
        //given
        Integer productId = 1;

        //when
        Flux<ProductAggregate> serviceCalls = Flux
                .interval(Duration.ofSeconds(1))
                .take(Duration.ofMinutes(1))
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(i -> System.out.printf("\n\n-----Second %d-----\n", i))
                .flatMap(i ->
                        webTestClient.get()
                                .uri("/sec08/product/{id}", productId)
                                .exchange()

                                //then
                                .expectStatus().isOk()
                                .returnResult(ProductAggregate.class)
                                .getResponseBody()
                );

        StepVerifier.create(serviceCalls)
                .thenConsumeWhile(pa -> true, aggregate -> assertAll(
                        () -> assertThat(aggregate)
                                .hasNoNullFieldsOrProperties()
                                .hasFieldOrPropertyWithValue("id", productId),
                        () -> assertThat(aggregate.getReviews())
                                .allSatisfy(review -> assertThat(review).hasNoNullFieldsOrProperties()),
                        () -> log.debug("Aggregate: {}", aggregate)
                ))
                .verifyComplete();
    }

}