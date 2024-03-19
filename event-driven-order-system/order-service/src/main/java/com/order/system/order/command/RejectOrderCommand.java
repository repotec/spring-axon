package com.order.system.order.command;

import lombok.Value;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class RejectOrderCommand {
    @TargetAggregateIdentifier
    private String orderId;
    private String reason;
}