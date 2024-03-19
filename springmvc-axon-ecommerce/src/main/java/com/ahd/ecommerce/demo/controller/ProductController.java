package com.ahd.ecommerce.demo.controller;

import com.ahd.ecommerce.demo.command.AddProductCommand;
import com.ahd.ecommerce.demo.read.model.ProductEntity;
import com.ahd.ecommerce.demo.read.query.FindProductQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping
    public void createProduct(@RequestBody ProductEntity product){
        AddProductCommand command =
                AddProductCommand.builder().id(product.getId())
                                           .price(product.getPrice())
                                           .stock(product.getStock())
                                           .description(product.getDescription())
                                  .build();
        commandGateway.sendAndWait(command);
    }

    @GetMapping
    public CompletableFuture<List<ProductEntity>> findProducts(){
        return queryGateway.query(new FindProductQuery(), ResponseTypes.multipleInstancesOf(ProductEntity.class));
    }
}
