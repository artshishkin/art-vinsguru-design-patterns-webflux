package net.shyshkin.study.webfluxpatterns.sec09.dto;

import lombok.Data;

@Data
public class Review {

    private Integer id;
    private String comment;
    private String user;
    private Integer rating;

}
