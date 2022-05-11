package net.shyshkin.study.webfluxpatterns.sec06.client;

import net.shyshkin.study.webfluxpatterns.sec06.dto.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ReviewClient {

    private final WebClient webClient;

    public ReviewClient(
            WebClient.Builder builder,
            @Value("${app.external.service.url.sec06.review}") String serverUrl) {
        webClient = builder.baseUrl(serverUrl).build();
    }

    public Mono<List<Review>> getReviews(Integer id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToFlux(Review.class)
                .collectList()
                .onErrorReturn(List.of());
    }
}
