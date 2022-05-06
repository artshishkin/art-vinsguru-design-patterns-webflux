package net.shyshkin.study.webfluxpatterns.sec03.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.sec03.dto.ShippingRequest;
import net.shyshkin.study.webfluxpatterns.sec03.dto.ShippingResponse;
import net.shyshkin.study.webfluxpatterns.sec03.dto.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ShippingClient {

    private static final String SCHEDULE = "/schedule";
    private static final String CANCEL = "/cancel";

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ShippingClient(
            WebClient.Builder builder,
            @Value("${app.external.service.url.sec03.shipping}") String serverUrl,
            ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        webClient = builder.baseUrl(serverUrl).build();
    }

    public Mono<ShippingResponse> schedule(ShippingRequest request) {
        return this.callShippingService(SCHEDULE, request);
    }

    public Mono<ShippingResponse> cancel(ShippingRequest request) {
        return this.callShippingService(CANCEL, request);
    }

    private Mono<ShippingResponse> callShippingService(String endpoint, ShippingRequest request) {
        return webClient
                .post()
                .uri(endpoint)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ShippingResponse.class)
                .onErrorResume(
                        error -> error instanceof WebClientResponseException && ((WebClientResponseException) error).getStatusCode().is5xxServerError(),
                        error -> extractResponse((WebClientResponseException) error))
                .onErrorReturn(fallbackShippingResponse(request));
    }

    private Mono<ShippingResponse> extractResponse(WebClientResponseException error) {
        ShippingResponse response;
        try {
            String responseBody = error.getResponseBodyAsString();
            log.debug("Error with response body: {}", responseBody);
            response = objectMapper.readValue(responseBody, ShippingResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
        return Mono.just(response);
    }

    private ShippingResponse fallbackShippingResponse(ShippingRequest request) {
        return ShippingResponse.builder()
                .address(null)
                .orderId(request.getOrderId())
                .expectedDelivery(null)
                .quantity(request.getQuantity())
                .status(Status.FAILED)
                .build();
    }

}
