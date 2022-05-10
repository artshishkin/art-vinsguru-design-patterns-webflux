package net.shyshkin.study.webfluxpatterns.sec05.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomReservationRequest {
    private String city;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private RoomCategory category;
}
