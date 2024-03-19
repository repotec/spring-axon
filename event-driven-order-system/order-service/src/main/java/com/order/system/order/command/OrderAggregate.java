package com.order.system.order.command;

import com.order.system.order.core.data.OrderStatus;
import com.order.system.order.core.events.OrderApprovedEvent;
import com.order.system.order.core.events.OrderCreatedEvent;
import com.order.system.order.core.events.OrderRejectedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@Slf4j
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;

    public OrderAggregate(){

    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand){
        log.info("CommandHandler|CreateOrderCommand for orderId:{}", createOrderCommand.getOrderId());
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);
        AggregateLifecycle.apply(orderCreatedEvent);
    }

    @CommandHandler
    public void handler(ApproveOrderCommand approveOrderCommand){
        log.info("CommandHandler|ApproveOrderCommand for orderId:{}", approveOrderCommand.getOrderId());
        OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(approveOrderCommand.getOrderId());
        AggregateLifecycle.apply(orderApprovedEvent);
    }

    @CommandHandler
    public void on (RejectOrderCommand rejectOrderCommand){
        OrderRejectedEvent orderRejectedEvent =
                new OrderRejectedEvent(rejectOrderCommand.getOrderId(), rejectOrderCommand.getReason());
        AggregateLifecycle.apply(orderRejectedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent orderCreatedEvent){
        this.orderId = orderCreatedEvent.getOrderId();
        this.productId = orderCreatedEvent.getProductId();
        this.userId = orderCreatedEvent.getUserId();
        this.quantity = orderCreatedEvent.getQuantity();
        this.addressId = orderCreatedEvent.getAddressId();
        this.orderStatus = orderCreatedEvent.getOrderStatus();
    }
    @EventSourcingHandler
    public void on(OrderApprovedEvent orderApprovedEvent){
        this.orderStatus = orderApprovedEvent.getOrderStatus();
    }

    @EventSourcingHandler
    public void on(OrderRejectedEvent orderRejectedEvent){
        this.orderStatus = orderRejectedEvent.getOrderStatus();
    }
}
