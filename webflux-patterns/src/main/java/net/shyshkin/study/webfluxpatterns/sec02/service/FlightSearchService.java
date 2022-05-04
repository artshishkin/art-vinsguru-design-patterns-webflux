package net.shyshkin.study.webfluxpatterns.sec02.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.sec02.client.DeltaClient;
import net.shyshkin.study.webfluxpatterns.sec02.client.FrontierClient;
import net.shyshkin.study.webfluxpatterns.sec02.client.JetBlueClient;
import net.shyshkin.study.webfluxpatterns.sec02.dto.FlightResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightSearchService {

    private final DeltaClient deltaClient;
    private final FrontierClient frontierClient;
    private final JetBlueClient jetBlueClient;

    public Flux<FlightResult> getFlights(String from, String to) {
        return Flux
                .merge(
                        deltaClient.getFlights(from, to),
                        frontierClient.getFlights(from, to),
                        jetBlueClient.getFlights(from, to)
                )
                .take(Duration.ofSeconds(3));
    }
}
