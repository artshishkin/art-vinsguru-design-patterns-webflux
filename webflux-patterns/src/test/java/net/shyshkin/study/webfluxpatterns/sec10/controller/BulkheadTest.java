package net.shyshkin.study.webfluxpatterns.sec10.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.sec10.dto.ProductAggregate;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled("Only for manual test. Start External Services (`docker-compose up`) and WebFluxPatternsApplication with profile `sec10`")
class BulkheadTest {

    WebClient webClient;

    @BeforeAll
    void beforeAll() {
        webClient = WebClient.builder()
                .baseUrl("http://localhost:8080/sec10")
                .build();
    }

    @Test
    @DisplayName("With 4 parallel calls")
    void concurrentUsersTest_4() {

        //when
        StepVerifier.create(Flux.merge(fibRequests(2), productRequests(2)))

                //then
                .verifyComplete();
    }

    @Test
    @DisplayName("With 60 parallel calls")
    void concurrentUsersTest_60() {

        //when
        StepVerifier.create(Flux.merge(fibRequests(30), productRequests(30)))

                //then
                .verifyComplete();
    }

    private Mono<Void> fibRequests(int parallelCallsCount) {
        return Mono.delay(Duration.ofMillis(100))
                .thenMany(Flux.range(1, parallelCallsCount))
                .flatMap(i -> webClient.get()
                        .uri("/fib/45")
                        .retrieve()
                        .bodyToMono(Long.class)
                        .doOnNext(result -> log.debug("Fib result: {}", result))
                )
                .then();
    }

    private Mono<Void> productRequests(int parallelCallsCount) {
        return Flux.range(1, parallelCallsCount)
                .flatMap(i -> webClient.get()
                        .uri("/product/1")
                        .retrieve()
                        .bodyToMono(ProductAggregate.class)
                        .map(ProductAggregate::getCategory)
                        .doOnNext(result -> log.debug("Product result: {}", result))
                )
                .then();
    }

}