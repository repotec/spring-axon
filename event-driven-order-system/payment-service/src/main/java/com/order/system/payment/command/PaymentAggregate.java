package com.order.system.payment.command;

import com.order.system.core.command.ProcessPaymentCommand;
import com.order.system.core.events.PaymentProcessedEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
@Slf4j
public class PaymentAggregate {
    @AggregateIdentifier
    private String paymentId;
    private String orderId;

    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand processPaymentCommand){
        log.info("**  CommandHandler|ProcessPaymentCommand|orderId:{}", processPaymentCommand.getOrderId());
        if(processPaymentCommand.getPaymentDetails() == null) {
            throw new IllegalArgumentException("Missing payment details");
        }

        if(processPaymentCommand.getOrderId() == null) {
            throw new IllegalArgumentException("Missing orderId");
        }

        if(processPaymentCommand.getPaymentId() == null) {
            throw new IllegalArgumentException("Missing paymentId");
        }

        //fire paymentProcessed Event
        PaymentProcessedEvent paymentProcessedEvent = PaymentProcessedEvent.builder().orderId(processPaymentCommand.getOrderId())
                .paymentId(processPaymentCommand.getPaymentId()).build();
        AggregateLifecycle.apply(paymentProcessedEvent);
    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent paymentProcessedEvent){
        this.orderId = paymentProcessedEvent.getOrderId();
        this.paymentId = paymentProcessedEvent.getPaymentId();
    }

}
