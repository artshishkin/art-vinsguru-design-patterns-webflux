package net.shyshkin.study.webfluxpatterns.sec02.client;

import net.shyshkin.study.webfluxpatterns.sec02.dto.FlightResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class JetBlueClient {

    public static final String JETBLUE = "JETBLUE";
    private final WebClient webClient;

    public JetBlueClient(
            @Value("${app.external.service.url.jetblue}") String serverUrl,
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
                .doOnNext(flightResult -> normalizeResponse(flightResult, from, to))
                .onErrorResume(ex -> Mono.empty());
    }

    private void normalizeResponse(FlightResult flightResult, String from, String to) {
        flightResult.setFrom(from);
        flightResult.setTo(to);
        flightResult.setAirline(JETBLUE);
    }

}
