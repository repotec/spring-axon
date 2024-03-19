package com.order.system.product.core.exception;

import org.axonframework.commandhandling.CommandExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class ProductServiceErrorHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException ex, WebRequest request){
        ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), new Date());
        return new ResponseEntity<>(errorMessage, new HttpHeaders(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(IllegalStateException ex, WebRequest request){
        ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), new Date());
        return new ResponseEntity<>(errorMessage, new HttpHeaders(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CommandExecutionException.class)
    public ResponseEntity<?> handleCommandExecutionException(CommandExecutionException ex, WebRequest request){
        ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), new Date());
        return new ResponseEntity<>(errorMessage, new HttpHeaders(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
