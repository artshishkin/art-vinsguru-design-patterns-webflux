package net.shyshkin.study.webfluxpatterns.sec09.client;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.sec09.dto.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class ReviewClient {

    private final WebClient webClient;

    public ReviewClient(
            WebClient.Builder builder,
            @Value("${app.external.service.url.sec09.review}") String serverUrl) {
        webClient = builder.baseUrl(serverUrl).build();
    }

    @RateLimiter(name = "review-service", fallbackMethod = "fallback")
    public Mono<List<Review>> getReviews(Integer id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.empty())
                .bodyToFlux(Review.class)
                .collectList();
    }

    public Mono<List<Review>> fallback(Integer id, Throwable ex) {
        log.debug("Rate Limit for Review Service with exception: {}", ex.toString());
        return Mono.just(List.of());
    }
}
