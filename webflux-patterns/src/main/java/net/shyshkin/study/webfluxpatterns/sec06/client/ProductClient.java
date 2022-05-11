package net.shyshkin.study.webfluxpatterns.sec06.client;

import net.shyshkin.study.webfluxpatterns.sec06.dto.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ProductClient {

    private final WebClient webClient;

    public ProductClient(
            WebClient.Builder builder,
            @Value("${app.external.service.url.sec06.product}") String serverUrl) {
        webClient = builder.baseUrl(serverUrl).build();
    }

    public Mono<Product> getProduct(Integer id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Product.class)
                .onErrorResume(ex -> Mono.empty());
    }
}
