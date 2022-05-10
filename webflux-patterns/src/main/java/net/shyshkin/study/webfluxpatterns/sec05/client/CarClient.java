package net.shyshkin.study.webfluxpatterns.sec05.client;

import net.shyshkin.study.webfluxpatterns.sec05.dto.CarReservationRequest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.CarReservationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CarClient {

    private static final String RESERVE = "/reserve";

    private final WebClient webClient;

    public CarClient(
            WebClient.Builder builder,
            @Value("${app.external.service.url.sec05.car}") String serverUrl) {
        webClient = builder.baseUrl(serverUrl).build();
    }

    public Flux<CarReservationResponse> reserve(Flux<CarReservationRequest> requestFlux) {
        return webClient
                .post()
                .uri(RESERVE)
                .body(requestFlux, CarReservationRequest.class)
                .retrieve()
                .bodyToFlux(CarReservationResponse.class)
                .onErrorResume(ex -> Mono.empty());
    }

}
