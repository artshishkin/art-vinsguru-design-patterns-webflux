package net.shyshkin.study.webfluxpatterns.sec05.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.sec05.client.CarClient;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationItemRequest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationItemResponse;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationType;
import net.shyshkin.study.webfluxpatterns.sec05.mapper.CarMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarReservationHandler extends ReservationHandler {

    private final CarClient client;
    private final CarMapper carMapper;

    @Override
    protected ReservationType getType() {
        return ReservationType.CAR;
    }

    @Override
    protected Flux<ReservationItemResponse> reserve(Flux<ReservationItemRequest> flux) {
//        return client.reserve(flux.map(carMapper::toCarRequest))
//                .map(carMapper::toItemResponse);
        return flux
                .map(carMapper::toCarRequest)
                .transform(client::reserve)
                .map(carMapper::toItemResponse)
                .onErrorContinue((ex, obj) -> log.debug("Ex: {}, obj: {}", ex, obj));
    }
}
