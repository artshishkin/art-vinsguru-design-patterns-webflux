package net.shyshkin.study.webfluxpatterns.sec08.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.sec08.dto.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class ReviewClient {

    private final WebClient webClient;

    public ReviewClient(
            WebClient.Builder builder,
            @Value("${app.external.service.url.sec08.review}") String serverUrl) {
        webClient = builder.baseUrl(serverUrl).build();
    }

    @CircuitBreaker(name = "review-service", fallbackMethod = "fallbackReviews")
    public Mono<List<Review>> getReviews(Integer id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.empty())
                .bodyToFlux(Review.class)
                .collectList()
                .doOnError(ex -> log.debug("Ex: {}", ex.toString()))
//                .retry(5)
                .retryWhen(Retry.fixedDelay(6, Duration.ofMillis(50)))
                .timeout(Duration.ofMillis(700));
    }

    private Mono<List<Review>> fallbackReviews(Integer id, Throwable ex) {
        log.debug("Fallback reviews called with error: {}", ex.toString());
        return Mono.just(List.of());
    }
}
