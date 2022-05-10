package net.shyshkin.study.webfluxpatterns.sec05.mapper;

import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationItemRequest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationItemResponse;
import net.shyshkin.study.webfluxpatterns.sec05.dto.RoomReservationRequest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.RoomReservationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomReservationResponse toResponse(RoomReservationRequest request);

    @Mapping(target = "checkIn", source = "from")
    @Mapping(target = "checkOut", source = "to")
    RoomReservationRequest toRoomRequest(ReservationItemRequest request);

    @Mapping(target = "from", source = "checkIn")
    @Mapping(target = "to", source = "checkOut")
    @Mapping(target = "itemId", source = "reservationId")
    @Mapping(target = "type", constant = "ROOM")
    ReservationItemResponse toItemResponse(RoomReservationResponse response);

}
