package com.ahd.ecommerce.demo.write;

import com.ahd.ecommerce.demo.command.CreateOrderCommand;
import com.ahd.ecommerce.demo.event.OrderCreatedEvent;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;

@Aggregate
@NoArgsConstructor
@Slf4j
@Data
public class Order {
    @AggregateIdentifier
    private UUID orderId;
    private Double price;
    private Integer quantity;
    private String productId;

    @CommandHandler
    public Order(CreateOrderCommand cmd) {
        log.info("CreateOrderCommand Handler");
        AggregateLifecycle.apply(OrderCreatedEvent.builder().orderId(cmd.getOrderId())
                                                            .quantity(cmd.getQuantity())
                                                            .price(cmd.getPrice())
                                                            .productId(cmd.getProductId())
                                                   .build());
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event){
        this.orderId = event.getOrderId();
        this.price = event.getPrice();
        this.quantity = event.getQuantity();
        this.productId = event.getProductId();
    }
}