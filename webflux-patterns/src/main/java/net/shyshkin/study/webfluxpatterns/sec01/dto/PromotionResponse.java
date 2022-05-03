package net.shyshkin.study.webfluxpatterns.sec01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionResponse {

    private Integer id;
    private String type;
    private LocalDate endDate;
    private Double discount;

}
