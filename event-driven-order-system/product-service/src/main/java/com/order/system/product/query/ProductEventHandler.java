package com.order.system.product.query;

import com.order.system.core.events.ProductCancelledReservationEvent;
import com.order.system.product.core.events.ProductCreatedEvent;
import com.order.system.product.core.data.Product;
import com.order.system.product.core.data.ProductRepository;
import com.order.system.core.events.ProductReservedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
@ProcessingGroup("product-group")
public class ProductEventHandler {
    private final ProductRepository productRepository;

    /**
     * Query Model
     *
     * Consume ProductCreatedEvent by @EventHandler and persist product details into the read database.
     * Individual process to Handle ProductCreatedEvent event from Axon (that sent by Command event in ProductAggregate)
     * and then persist product into db.
     *
     * it just like a listener which listen and grab the new Commands with same type of ProductCreatedEvent
     * and then store them into database
     */
    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent) /*throws Exception*/ {
        log.info("**  ProductReservedEvent event handler for productTitle:{}", productCreatedEvent.getTitle());

        Product product = new Product();
        BeanUtils.copyProperties(productCreatedEvent, product);

        try {
            productRepository.save(product);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

        /**
         * Axon framework will postpone ProductCreatedEvent and prevent EventHandler to do their job if
         * an exception took place even after productRepository.save method
         *
         * this is mean if any exception raised even in the end of method the event will get stored in Axon server
         * and productRepository.save will not be executed as well
         */
        //if(true) throw new Exception("Something went wrong in Event Handler class");
    }

    /**
     * Consume ProductReservedEvent by @EventHandler:
     * Individual process to Handle ProductReservedEvent event from Axon (that sent by Command event in ProductAggregate)
     * and then update product quantity and persist it into db.
     *
     * it just like a listener which listen and grab the new Commands with same type of ProductReservedEvent
     * and then  update product quantity and persist it into db.
     */
    @EventHandler
    public void on(ProductReservedEvent productReservedEvent) /*throws Exception*/ {
        log.info("**  ProductReservedEvent event handler for productId:{} and orderId{}:", productReservedEvent.getProductId(), productReservedEvent.getOrderId());

        Optional<Product> product = productRepository.findById(productReservedEvent.getProductId());

        try {
            log.info("**  ProductReservedEvent handler will decrease product quantity by {}", productReservedEvent.getQuantity());
            Product newProduct = product.get();
            newProduct.setQuantity(newProduct.getQuantity() - productReservedEvent.getQuantity());
            productRepository.save(newProduct);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

        /**
         * Axon framework will postpone ProductCreatedEvent and prevent EventHandler to do their job if
         * an exception took place even after productRepository.save method
         *
         * this is mean if any exception raised even in the end of method the event will get stored in Axon server
         * and productRepository.save will not be executed as well
         */
        //if(true) throw new Exception("Something went wrong in Event Handler class");
    }

    @EventHandler
    public void on (ProductCancelledReservationEvent productCancelledReservationEvent){
        Optional<Product> product = productRepository.findById(productCancelledReservationEvent.getProductId());

        try {
            log.info("**  ProductCancelledReservationEvent handler will increase product quantity by {}", productCancelledReservationEvent.getQuantity());
            Product newProduct = product.get();
            newProduct.setQuantity(newProduct.getQuantity() + productCancelledReservationEvent.getQuantity());
            productRepository.save(newProduct);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }


    /**
     * if any of the methods in our EventHandler class throws an IllegalArgumentException,then this method will be triggered,
     * and an exception message can be logged.
     * This method with the ExceptionHandler annotation will only handle exceptions that are thrown from event handling
     * functions in this same class. so if an exception is thrown in some other place in your application, then this method
     * will not be able to handle that exception.
     *
     * Handler is configured to use subscribing event processor. these events are processed in the same thread and
     * processing events in the same thread gives this possibility to roll back the whole transaction, if an exception
     * takes place.
     *
     * We will need to either not handle the exception or we will need to handle it and then do something about it
     * and then rethrow it.
     */
    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handleIllegalArgumentException(IllegalArgumentException exception){
        log.error("**  error caught by handleIllegalArgumentException", exception);
        throw exception;
    }

    /**
     * To handle general exceptions
     */
    @ExceptionHandler()
    public void handleGeneralException(Exception exception) throws Exception {
        log.error("**  error caught by handleGeneralException", exception);
        throw exception;
    }
}
