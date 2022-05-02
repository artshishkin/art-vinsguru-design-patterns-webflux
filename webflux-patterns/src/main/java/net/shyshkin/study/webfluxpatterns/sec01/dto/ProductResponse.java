package net.shyshkin.study.webfluxpatterns.sec01.dto;

import lombok.Data;

@Data
public class ProductResponse {

    private Integer id;
    private String category;
    private String description;
    private Integer price;

}
