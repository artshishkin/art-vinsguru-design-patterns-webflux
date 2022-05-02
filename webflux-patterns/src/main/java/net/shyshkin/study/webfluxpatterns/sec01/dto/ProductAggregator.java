package net.shyshkin.study.webfluxpatterns.sec01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAggregator {

    private Integer id;
    private String category;
    private String description;
    private Price price;
    private List<Review> reviews;

}
