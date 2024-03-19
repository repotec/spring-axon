package com.order.system.order.query;

import com.order.system.order.core.data.Order;
import com.order.system.order.core.data.OrderRepository;
import com.order.system.order.core.data.OrderSummary;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderQueriesHandler {

    private final OrderRepository orderRepository;

    @QueryHandler
    public OrderSummary findOrder(FindOrderQuery findOrderQuery){
        Order order = orderRepository.findById(findOrderQuery.getOrderId()).orElseThrow(RuntimeException::new);
        return new OrderSummary(order.getOrderId(), order.getOrderStatus(), null);
    }
}
