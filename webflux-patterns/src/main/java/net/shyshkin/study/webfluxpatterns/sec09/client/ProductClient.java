package net.shyshkin.study.webfluxpatterns.sec09.client;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.sec09.dto.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ProductClient {

    private final WebClient webClient;

    public ProductClient(
            WebClient.Builder builder,
            @Value("${app.external.service.url.sec09.product}") String serverUrl) {
        webClient = builder.baseUrl(serverUrl).build();
    }

    public Mono<Product> getProduct(Integer id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnError(ex -> log.debug("Ex: {}", ex.toString()))
                .onErrorResume(ex -> Mono.empty());
    }
}
