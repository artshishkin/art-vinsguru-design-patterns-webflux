package net.shyshkin.study.webfluxpatterns.sec10.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec10.dto.ProductAggregate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("sec10")
class BulkheadTest extends ExternalServiceAbstractTest {

    WebClient webClient;

    @LocalServerPort
    int randomServerPort;

    @BeforeEach
    void beforeAll() {
        webClient = WebClient.builder()
                .baseUrl("http://localhost:" + randomServerPort)
                .build();
    }

    @Test
    @DisplayName("With 40 parallel calls")
    void concurrentUsersTest_40() {

        //given
        AtomicInteger counter = new AtomicInteger(0);
        //when
        Flux<String> pipeline = Flux
                .merge(fibRequests(20), productRequests(20))
                .doOnNext(resp -> log.debug("{}", resp));
        StepVerifier.create(pipeline)

                //then
                .thenConsumeWhile(
                        res -> counter.incrementAndGet() <= 20,
                        res -> assertThat(res).startsWith("Product result: ")
                )
                .thenConsumeWhile(
                        res -> counter.incrementAndGet() <= 31, //one extra 1 because of previous increment
                        res -> assertThat(res).startsWith("Fib result: ")
                )
                .expectNextCount(10)
                .verifyComplete();
    }

    private Flux<String> fibRequests(int parallelCallsCount) {
        return Flux.range(1, parallelCallsCount)
                .doOnSubscribe(sub -> log.debug("Fibonacci requests started"))
                .flatMap(i -> webClient.get()
                        .uri("/sec10/fib/40")
                        .retrieve()
                        .bodyToMono(Long.class)
                )
                .map(String::valueOf)
                .map("Fib result: "::concat);
    }

    private Flux<String> productRequests(int parallelCallsCount) {
        return Mono.delay(Duration.ofMillis(100))
                .thenMany(Flux.range(1, parallelCallsCount))
                .doOnSubscribe(sub -> log.debug("Product requests started"))
                .flatMap(i -> webClient.get()
                        .uri("/sec10/product/1")
                        .retrieve()
                        .bodyToMono(ProductAggregate.class))
                .map(ProductAggregate::getCategory)
                .map("Product result: "::concat);
    }

}