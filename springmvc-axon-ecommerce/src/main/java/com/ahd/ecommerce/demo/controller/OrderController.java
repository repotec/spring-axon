package com.ahd.ecommerce.demo.controller;

import com.ahd.ecommerce.demo.command.CreateOrderCommand;
import com.ahd.ecommerce.demo.read.model.OrderEntity;
import com.ahd.ecommerce.demo.read.query.FindOrderQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping
    public void createNewOrder(@RequestParam String productId,
                               @RequestParam Double price,
                               @RequestParam Integer quantity){
        log.info("createNewOrder POST API");
        CreateOrderCommand command = CreateOrderCommand.builder().orderId(UUID.randomUUID())
                                                                 .price(price)
                                                                 .quantity(quantity)
                                                                 .productId(productId)
                                                       .build();

//        queryGateway.subscriptionQuery(new FindOrderQuery(), ResponseTypes.multipleInstancesOf(OrderEntity.class),
//                                                             ResponseTypes.multipleInstancesOf(OrderEntity.class));

        commandGateway.sendAndWait(command);
    }

    @GetMapping
    public CompletableFuture<List<OrderEntity>> findAllProducts(){
        log.info("findAllProducts");
        return queryGateway.query(new FindOrderQuery(), ResponseTypes.multipleInstancesOf(OrderEntity.class));
    }
}
