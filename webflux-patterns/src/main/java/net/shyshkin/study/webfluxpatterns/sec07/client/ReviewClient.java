package net.shyshkin.study.webfluxpatterns.sec07.client;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.sec07.dto.Review;
import org.springframework.beans.factory.annotation.Value;
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
            @Value("${app.external.service.url.sec07.review}") String serverUrl) {
        webClient = builder.baseUrl(serverUrl).build();
    }

    public Mono<List<Review>> getReviews(Integer id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToFlux(Review.class)
                .collectList()
                .doOnError(ex -> log.debug("Ex: {}", ex.toString()))
                .onErrorReturn(List.of());
    }
}
