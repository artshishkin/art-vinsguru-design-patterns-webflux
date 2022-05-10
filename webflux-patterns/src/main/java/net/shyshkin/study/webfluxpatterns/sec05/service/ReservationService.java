package net.shyshkin.study.webfluxpatterns.sec05.service;

import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationItemRequest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationItemResponse;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationResponse;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final Map<ReservationType, ReservationHandler> handlerMap;

    public ReservationService(List<ReservationHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(ReservationHandler::getType, Function.identity()));
    }

    public Mono<ReservationResponse> reserve(Flux<ReservationItemRequest> requests) {
        return requests
                .groupBy(ReservationItemRequest::getType) //splitter
                .flatMap(this::aggregator)
                .collectList()
                .map(this::toReservationResponse);
    }

    private Flux<ReservationItemResponse> aggregator(GroupedFlux<ReservationType, ReservationItemRequest> groupedFlux) {
        var reservationType = groupedFlux.key();
        var reservationHandler = handlerMap.get(reservationType);
        return reservationHandler.reserve(groupedFlux);
    }

    private ReservationResponse toReservationResponse(List<ReservationItemResponse> list) {
        return ReservationResponse.create(
                UUID.randomUUID(),
                list.stream().mapToInt(ReservationItemResponse::getPrice).sum(),
                list
        );
    }

}
