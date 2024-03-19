package com.ahd.ecommerce.demo.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@Builder
public class CreateOrderCommand {
    @TargetAggregateIdentifier
    private UUID orderId;
    private Double price;
    private Integer quantity;
    private String productId;
}
