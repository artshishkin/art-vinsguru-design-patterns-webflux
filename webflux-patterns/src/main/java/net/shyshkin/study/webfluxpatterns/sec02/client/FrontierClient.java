package net.shyshkin.study.webfluxpatterns.sec02.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.shyshkin.study.webfluxpatterns.sec02.dto.FlightResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FrontierClient {

    private final WebClient webClient;

    public FrontierClient(
            @Value("${app.external.service.url.frontier}") String serverUrl,
            WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl(serverUrl)
                .build();
    }

    public Flux<FlightResult> getFlights(String from, String to) {
//        var frontierInput = Map.of("from", from, "to", to);
        var frontierInput = FrontierRequest.create(from, to);
        return webClient.post()
                .bodyValue(frontierInput)
                .retrieve()
                .bodyToFlux(FlightResult.class)
                .onErrorResume(ex -> Mono.empty());
    }

    @Data
    @AllArgsConstructor(staticName = "create")
    private static class FrontierRequest {
        private String from;
        private String to;
    }

}
