package net.shyshkin.study.webfluxpatterns.sec03.dto;

import lombok.Data;

@Data
public class InventoryResponse {

    private Integer productId;
    private Integer quantity;
    private Integer remainingQuantity;
    private Status status;

}
