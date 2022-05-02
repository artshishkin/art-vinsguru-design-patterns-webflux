package net.shyshkin.study.webfluxpatterns.sec01.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PromotionResponse {

    private Integer id;
    private String type;
    private LocalDate endDate;
    private Double discount;

}
