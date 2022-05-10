package net.shyshkin.study.webfluxpatterns.sec05.client;

import net.shyshkin.study.webfluxpatterns.sec05.dto.RoomReservationRequest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.RoomReservationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RoomClient {

    private static final String RESERVE = "/reserve";

    private final WebClient webClient;

    public RoomClient(
            WebClient.Builder builder,
            @Value("${app.external.service.url.sec05.room}") String serverUrl) {
        webClient = builder.baseUrl(serverUrl).build();
    }

    public Flux<RoomReservationResponse> reserve(Flux<RoomReservationRequest> requestFlux) {
        return webClient
                .post()
                .uri(RESERVE)
                .body(requestFlux, RoomReservationRequest.class)
                .retrieve()
                .bodyToFlux(RoomReservationResponse.class)
                .onErrorResume(ex -> Mono.empty());
    }

}
