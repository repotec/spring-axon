package com.ahd.ecommerce.demo.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class UpdateStockCommand {
    @TargetAggregateIdentifier
    private String id;
    private Integer stock;
}
