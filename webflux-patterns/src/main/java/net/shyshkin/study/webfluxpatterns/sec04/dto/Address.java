package net.shyshkin.study.webfluxpatterns.sec04.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    private String street;
    private String city;
    private String state;
    private String zipCode;

}
