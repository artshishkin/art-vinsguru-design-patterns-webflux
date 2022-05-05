package net.shyshkin.study.webfluxpatterns.sec03.dto;

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
public class OrderResponse {

    private UUID orderId;
    private Integer userId;
    private Integer productId;
    private Status status;
    private Address address;
    private LocalDate expectedDelivery;
    
}
