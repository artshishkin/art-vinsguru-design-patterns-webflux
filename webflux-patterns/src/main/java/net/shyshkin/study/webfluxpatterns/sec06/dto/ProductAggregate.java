package net.shyshkin.study.webfluxpatterns.sec06.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAggregate {

    private Integer id;
    private String category;
    private String description;
    private List<Review> reviews;

}
