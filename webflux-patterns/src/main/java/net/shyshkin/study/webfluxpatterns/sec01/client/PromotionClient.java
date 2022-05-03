package net.shyshkin.study.webfluxpatterns.sec01.client;

import net.shyshkin.study.webfluxpatterns.sec01.dto.PromotionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Component
public class PromotionClient {

    public static final PromotionResponse NO_PROMOTION = PromotionResponse.builder()
            .id(-1)
            .discount(0.0)
            .type("NO_PROMOTION")
            .endDate(LocalDate.now())
            .build();

    private final WebClient webClient;

    public PromotionClient(
            WebClient.Builder builder,
            @Value("${app.external.service.url.promotion}") String serverUrl) {
        webClient = builder.baseUrl(serverUrl).build();
    }

    public Mono<PromotionResponse> getPromotion(Integer id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(PromotionResponse.class)
//                .onErrorResume(ex->Mono.empty())
                .onErrorReturn(NO_PROMOTION);
    }
}
