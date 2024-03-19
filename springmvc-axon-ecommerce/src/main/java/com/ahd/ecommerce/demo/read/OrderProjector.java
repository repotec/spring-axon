package com.ahd.ecommerce.demo.read;

import com.ahd.ecommerce.demo.event.OrderCreatedEvent;
import com.ahd.ecommerce.demo.event.StockUpdatedEvent;
import com.ahd.ecommerce.demo.read.model.OrderEntity;
import com.ahd.ecommerce.demo.read.query.FindOrderQuery;
import com.ahd.ecommerce.demo.read.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//events handler
public class OrderProjector {
    private final EventGateway eventGateway;
    private final OrderRepository orderRepository;

    @EventHandler
    public void on(OrderCreatedEvent event){
        log.info("OrderCreatedEvent handler");
        OrderEntity newOrder = OrderEntity.builder().orderId(event.getOrderId())
                                                    .price(event.getPrice())
                                                    .productId(event.getProductId())
                                                    .quantity(event.getQuantity())
                                                    .build();
        orderRepository.save(newOrder);

        //dispatching events from a Non-Aggregate - to decease stock quantity after purchase
        StockUpdatedEvent stockUpdatedEvent = StockUpdatedEvent.builder().id(event.getProductId())
                                                                         .stock(event.getQuantity())
                                                               .build();

        eventGateway.publish(stockUpdatedEvent);
    }

    @QueryHandler
    public List<OrderEntity> handle(FindOrderQuery query){
        List<OrderEntity> orders = (List<OrderEntity>) orderRepository.findAll();
        return orders;
    }
}
