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
public class Price {

    private Integer listPrice;
    private Double discount;
    private Double discountedPrice;
    private Double amountSaved;
    private LocalDate endDate;

}
