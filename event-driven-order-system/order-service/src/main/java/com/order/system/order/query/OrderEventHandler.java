package com.order.system.order.query;

import com.order.system.order.command.RejectOrderCommand;
import com.order.system.order.core.data.Order;
import com.order.system.order.core.data.OrderRepository;
import com.order.system.order.core.events.OrderApprovedEvent;
import com.order.system.order.core.events.OrderCreatedEvent;
import com.order.system.order.core.events.OrderRejectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@ProcessingGroup("order-group")
@Slf4j
public class OrderEventHandler {
    private final OrderRepository orderRepository;

    @EventHandler
    public void on(OrderCreatedEvent orderCreatedEvent){
        log.info("** OrderCreatedEvent|order has been created for orderId:{}", orderCreatedEvent.getOrderId());

        Order order = new Order();
        BeanUtils.copyProperties(orderCreatedEvent, order);
        orderRepository.save(order);
    }

    @EventHandler
    public void on(OrderApprovedEvent orderApprovedEvent){
        log.info("** OrderApprovedEvent|order has been approved for orderId:{}", orderApprovedEvent.getOrderId());

        Optional<Order> orderOptional = orderRepository.findById(orderApprovedEvent.getOrderId());

        if(!orderOptional.isPresent()){
            //need to do something
            return;
        }
        Order order = orderOptional.get();
        order.setOrderStatus(orderApprovedEvent.getOrderStatus());
        orderRepository.save(order);
    }

    public void on(OrderRejectedEvent orderRejectedEvent){
        log.info("** OrderApprovedEvent|order has been rejected for orderId:{},reason:{}", orderRejectedEvent.getOrderId(), orderRejectedEvent.getReason());

        Optional<Order> orderOptional = orderRepository.findById(orderRejectedEvent.getOrderId());
        if(!orderOptional.isPresent()){
            //need to do something
            return;
        }
        Order order = orderOptional.get();
        order.setOrderStatus(orderRejectedEvent.getOrderStatus());
        orderRepository.save(order);
    }
}
