package com.order.system.order.command.rest;

import com.order.system.order.command.CreateOrderCommand;
import com.order.system.order.core.data.OrderStatus;
import com.order.system.order.core.data.OrderSummary;
import com.order.system.order.query.FindOrderQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryMessage;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersCommandController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping
    public OrderSummary createOrder(@Valid @RequestBody CreateOrderRequestModel order){
        String orderId = UUID.randomUUID().toString();
        String userId = "27b95829-4f3f-4ddf-8983-151ba010e35b";

        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder().orderId(orderId)
                                                                            .userId(userId)
                                                                            .productId(order.getProductId())
                                                                            .addressId(order.getAddressId())
                                                                            .quantity(order.getQuantity())
                                                                            .orderStatus(OrderStatus.CREATED)
                                                                            .build();
        SubscriptionQueryResult<OrderSummary, OrderSummary> queryResult =
                        queryGateway.subscriptionQuery(new FindOrderQuery(orderId),
                                                      ResponseTypes.instanceOf(OrderSummary.class),
                                                      ResponseTypes.instanceOf(OrderSummary.class));
        try {
            commandGateway.sendAndWait(createOrderCommand);

            return queryResult.updates().blockFirst();
        }finally{
            queryResult.close();
        }
    }
}
