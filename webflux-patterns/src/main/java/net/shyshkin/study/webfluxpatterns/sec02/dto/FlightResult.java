package net.shyshkin.study.webfluxpatterns.sec02.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightResult {

    private String airline;
    private LocalDate date;
    private String from;
    private String to;
    private Double price;

}
