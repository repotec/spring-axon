package com.ahd.ecommerce.demo.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductAddedEvent {
    private String id;
    private Double price;
    private Integer stock;
    private String description;
}
