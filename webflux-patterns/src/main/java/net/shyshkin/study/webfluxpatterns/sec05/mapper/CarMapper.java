package net.shyshkin.study.webfluxpatterns.sec05.mapper;

import net.shyshkin.study.webfluxpatterns.sec05.dto.CarReservationRequest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.CarReservationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarMapper {

    CarReservationResponse toResponse(CarReservationRequest request);

}
