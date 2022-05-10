package net.shyshkin.study.webfluxpatterns.sec05.service;

import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationItemRequest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationItemResponse;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationType;
import reactor.core.publisher.Flux;

public abstract class ReservationHandler {

    protected abstract ReservationType getType();

    protected abstract Flux<ReservationItemResponse> reserve(Flux<ReservationItemRequest> flux);
}
