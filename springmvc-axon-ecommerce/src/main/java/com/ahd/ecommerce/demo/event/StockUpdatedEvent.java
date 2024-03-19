package com.ahd.ecommerce.demo.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockUpdatedEvent {
    private String id;
    private Integer stock;
}
