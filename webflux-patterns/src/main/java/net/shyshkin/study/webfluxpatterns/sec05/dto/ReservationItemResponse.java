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
public class ReservationItemResponse {

    private ReservationType type;
    private UUID itemId;
    private String city;
    private LocalDate from;
    private LocalDate to;
    private String category;
    private Integer price;

}
