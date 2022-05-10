package net.shyshkin.study.webfluxpatterns.sec05.mapper;

import net.shyshkin.study.webfluxpatterns.sec05.dto.RoomReservationRequest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.RoomReservationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomReservationResponse toResponse(RoomReservationRequest request);

}
