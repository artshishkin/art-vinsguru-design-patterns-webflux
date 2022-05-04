package net.shyshkin.study.webfluxpatterns.sec02.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webfluxpatterns.sec02.dto.FlightResult;
import net.shyshkin.study.webfluxpatterns.sec02.service.FlightSearchService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("sec02")
public class FlightsController {

    private final FlightSearchService service;

    @GetMapping(value = "flights/{from}/{to}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<FlightResult> searchFlights(@PathVariable String from, @PathVariable String to) {
        return service.getFlights(from, to);
    }

}
