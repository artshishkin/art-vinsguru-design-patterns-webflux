package net.shyshkin.study.webfluxpatterns.sec05.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.sec05.client.RoomClient;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationItemRequest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationItemResponse;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationType;
import net.shyshkin.study.webfluxpatterns.sec05.mapper.RoomMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomReservationHandler extends ReservationHandler {

    private final RoomClient client;
    private final RoomMapper roomMapper;

    @Override
    protected ReservationType getType() {
        return ReservationType.ROOM;
    }

    @Override
    protected Flux<ReservationItemResponse> reserve(Flux<ReservationItemRequest> flux) {
        return flux
                .map(roomMapper::toRoomRequest)
                .transform(client::reserve)
                .map(roomMapper::toItemResponse)
                .onErrorContinue((ex, obj) -> log.debug("Ex: {}, obj: {}", ex, obj));
    }
}
