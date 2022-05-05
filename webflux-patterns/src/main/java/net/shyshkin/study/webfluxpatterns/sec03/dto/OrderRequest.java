package net.shyshkin.study.webfluxpatterns.sec03.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    private Integer userId;
    private Integer productId;
    private Integer quantity;

}
