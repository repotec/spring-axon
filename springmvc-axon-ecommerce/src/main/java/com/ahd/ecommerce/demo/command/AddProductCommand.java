package com.ahd.ecommerce.demo.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class AddProductCommand {
    @TargetAggregateIdentifier
    private String id;
    private Double price;
    private Integer stock;
    private String description;
}