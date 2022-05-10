package net.shyshkin.study.webfluxpatterns.sec05.mapper;

import net.shyshkin.study.webfluxpatterns.sec05.dto.CarReservationRequest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.CarReservationResponse;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationItemRequest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarMapper {

    CarReservationResponse toResponse(CarReservationRequest request);

    @Mapping(target = "pickup", source = "from")
    @Mapping(target = "drop", source = "to")
    CarReservationRequest toCarRequest(ReservationItemRequest request);

    @Mapping(target = "from", source = "pickup")
    @Mapping(target = "to", source = "drop")
    @Mapping(target = "itemId", source = "reservationId")
    @Mapping(target = "type", constant = "CAR")
    ReservationItemResponse toItemResponse(CarReservationResponse response);

}
