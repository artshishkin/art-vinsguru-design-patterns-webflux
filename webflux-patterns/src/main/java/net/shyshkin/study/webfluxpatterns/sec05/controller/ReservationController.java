package net.shyshkin.study.webfluxpatterns.sec05.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationItemRequest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationResponse;
import net.shyshkin.study.webfluxpatterns.sec05.service.ReservationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("sec05/reserve")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public Mono<ReservationResponse> reserve(@RequestBody Flux<ReservationItemRequest> requestFlux) {
        return reservationService.reserve(requestFlux);
    }

}
