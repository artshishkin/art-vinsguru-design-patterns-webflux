package net.shyshkin.study.webfluxpatterns.sec01.client;

import net.shyshkin.study.webfluxpatterns.sec01.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ProductClient {

    private final WebClient webClient;

    public ProductClient(
            WebClient.Builder builder,
            @Value("${app.external.service.url.product}") String serverUrl) {
        webClient = builder.baseUrl(serverUrl).build();
    }

    public Mono<ProductResponse> getProduct(Integer id) {
        return webClient.get()
                .uri("{id}", id)
                .retrieve()
                .bodyToMono(ProductResponse.class);
    }
}
