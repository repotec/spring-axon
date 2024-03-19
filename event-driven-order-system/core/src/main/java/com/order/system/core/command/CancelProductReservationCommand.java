package com.order.system.core.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class CancelProductReservationCommand {
    @TargetAggregateIdentifier
    private String productId;

    private String orderId;
    private String userId;
    private int quantity;
    private String reason;
}
