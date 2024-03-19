package com.ahd.ecommerce.demo.event;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Data
@Builder
public class OrderCreatedEvent {
    private UUID orderId;
    private Double price;
    private Integer quantity;
    private String productId;
}
