package net.shyshkin.study.webfluxpatterns.sec03.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ShippingResponse {

    private UUID orderId;
    private Integer quantity;
    private Status status;
    private LocalDate expectedDelivery;
    private Address address;

}
