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
public class RoomReservationResponse {
    private UUID reservationId;
    private String city;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private RoomCategory category;
    private Integer price;
}
