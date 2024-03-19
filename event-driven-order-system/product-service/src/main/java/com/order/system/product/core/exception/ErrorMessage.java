package com.order.system.product.core.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorMessage{
    private String message;
    private Date timestamp;
}
