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
public class InventoryRequest {

    private UUID paymentId;
    private Integer productId;
    private Integer quantity;

}
