package net.shyshkin.study.webfluxpatterns.sec02.client;

import net.shyshkin.study.webfluxpatterns.sec02.dto.FlightResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class DeltaClient {

    private final WebClient webClient;

    public DeltaClient(
            @Value("${app.external.service.url.delta}") String serverUrl,
            WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl(serverUrl)
                .build();
    }

    public Flux<FlightResult> getFlights(String from, String to) {
        return webClient.get()
                .uri("/{from}/{to}", from, to)
                .retrieve()
                .bodyToFlux(FlightResult.class)
                .onErrorResume(ex -> Mono.empty());
    }

}
