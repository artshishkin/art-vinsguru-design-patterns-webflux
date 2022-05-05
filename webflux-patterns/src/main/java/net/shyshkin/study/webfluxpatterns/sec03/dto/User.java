package net.shyshkin.study.webfluxpatterns.sec03.dto;

import lombok.Data;

@Data
public class User {
    private Integer userId;
    private String name;
    private Integer balance;
    private Address address;
}
