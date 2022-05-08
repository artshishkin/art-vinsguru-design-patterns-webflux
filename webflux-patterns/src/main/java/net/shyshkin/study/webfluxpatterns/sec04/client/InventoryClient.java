package net.shyshkin.study.webfluxpatterns.sec04.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.shyshkin.study.webfluxpatterns.sec04.dto.InventoryRequest;
import net.shyshkin.study.webfluxpatterns.sec04.dto.InventoryResponse;
import net.shyshkin.study.webfluxpatterns.sec04.dto.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class InventoryClient {

    private static final String DEDUCT = "/deduct";
    private static final String RESTORE = "/restore";

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public InventoryClient(
            WebClient.Builder builder,
            @Value("${app.external.service.url.sec04.inventory}") String serverUrl,
            ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        webClient = builder.baseUrl(serverUrl).build();
    }

    public Mono<Integer> getInventory(Integer id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Integer.class)
                .onErrorResume(ex -> Mono.empty());
    }

    public Mono<InventoryResponse> deduct(InventoryRequest request) {
        return this.callInventoryService(DEDUCT, request);
    }

    public Mono<InventoryResponse> restore(InventoryRequest request) {
        return this.callInventoryService(RESTORE, request);
    }

    private Mono<InventoryResponse> callInventoryService(String endpoint, InventoryRequest request) {
        return webClient
                .post()
                .uri(endpoint)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .onErrorResume(
                        error -> error instanceof WebClientResponseException && ((WebClientResponseException) error).getStatusCode().is5xxServerError(),
                        error -> extractResponse((WebClientResponseException) error))
                .onErrorReturn(fallbackInventoryResponse(request));
    }

    private Mono<InventoryResponse> extractResponse(WebClientResponseException error) {
        InventoryResponse response;
        try {
            response = objectMapper.readValue(error.getResponseBodyAsString(), InventoryResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
        return Mono.just(response);
    }

    private InventoryResponse fallbackInventoryResponse(InventoryRequest request) {
        return InventoryResponse.builder()
                .inventoryId(null)
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .remainingQuantity(null)
                .status(Status.FAILED)
                .build();
    }

}
