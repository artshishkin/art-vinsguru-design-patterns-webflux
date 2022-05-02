package net.shyshkin.study.webfluxpatterns.sec01.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Price {

    private Integer listPrice;
    private Double discount;
    private Double discountedPrice;
    private Double amountSaved;
    private LocalDate endDate;

}
