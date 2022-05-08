package net.shyshkin.study.webfluxpatterns.sec04.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Integer userId;
    private String name;
    private Integer balance;
    private Status status;

}
