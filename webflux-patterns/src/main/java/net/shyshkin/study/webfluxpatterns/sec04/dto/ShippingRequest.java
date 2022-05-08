package net.shyshkin.study.webfluxpatterns.sec04.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingRequest {

    private Integer quantity;
    private Integer userId;
    private UUID orderId;

}
