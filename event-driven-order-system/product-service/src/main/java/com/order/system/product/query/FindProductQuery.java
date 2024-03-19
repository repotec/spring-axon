package com.order.system.product.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

//Scatter-Gather Query
@Data
@Builder
@AllArgsConstructor
public class FindProductQuery {
    private String productId;
}
