package net.shyshkin.study.webfluxpatterns.sec04.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingResponse {

    private UUID orderId;
    private Integer quantity;
    private Status status;
    private LocalDate expectedDelivery;
    private Address address;

}
