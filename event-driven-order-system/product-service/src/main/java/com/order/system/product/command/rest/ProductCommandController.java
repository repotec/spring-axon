package com.order.system.product.command.rest;

import com.order.system.product.command.CreateProductCommand;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductCommandController {
    private final CommandGateway commandGateway;

    @PostMapping
    public String createProducts(@Valid @RequestBody CreateProductRequestModel createProductRequestModel){
        CreateProductCommand createProductCommand = CreateProductCommand.builder().title(createProductRequestModel.getTitle())
                                                                                  .price(createProductRequestModel.getPrice())
                                                                                  .quantity(createProductRequestModel.getQuantity())
                                                                                  .productId(UUID.randomUUID().toString())
                                                                        .build();

        String output = commandGateway.sendAndWait(createProductCommand);
        return output;
    }
}
