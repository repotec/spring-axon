package com.order.system.order.command.rest;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateOrderRequestModel {
    @NotBlank(message = "Product title is a required field")
    private String productId;
    private Integer quantity;
    private String addressId;
}
