package net.shyshkin.study.webfluxpatterns.sec05.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarReservationResponse {

    private UUID reservationId;
    private String city;
    private LocalDate pickup;
    private LocalDate drop;
    private CarCategory category;
    private Integer price;

}
