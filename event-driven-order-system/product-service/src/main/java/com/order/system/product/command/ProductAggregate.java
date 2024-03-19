package com.order.system.product.command;

import com.order.system.core.command.CancelProductReservationCommand;
import com.order.system.core.events.ProductCancelledReservationEvent;
import com.order.system.product.core.events.ProductCreatedEvent;
import com.order.system.core.command.ReserveProductCommand;
import com.order.system.core.events.ProductReservedEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

/**
 * magic annotation,
 * we say to Axon that this class in able to handel a Command related
 * and that is need to loaded through the Axon repository
 *
 * NoArgsConstructor is required by Axon framework
 */
@Aggregate
@NoArgsConstructor
@Slf4j
public class ProductAggregate {
    @AggregateIdentifier
    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;

    //Handler for createProductCommand that sent through CommandGateway in POST request
    @CommandHandler
    public ProductAggregate(CreateProductCommand createProductCommand) /*throws Exception*/ {
        log.info("** CreateProductCommand handler|productId:{}", createProductCommand.getProductId());
        //validate Create Product Command
        if(createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("price cannot be less or equal zero!");
        }

        if(createProductCommand.getTitle() == null || createProductCommand.getTitle().isEmpty()){
            throw new IllegalArgumentException("title cannot be null or empty");
        }

        //convert createProductCommand to productCreatedEvent
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();
        BeanUtils.copyProperties(createProductCommand, productCreatedEvent);
        AggregateLifecycle.apply(productCreatedEvent);

        /**
        * Axon framework will postpone ProductCreatedEvent and prevent CommandHandler and EventSourcingHandler
        * to do their job if an exception took place even after AggregateLifecycle.apply method
        */
        //if(true) throw new Exception("Something went wrong in Command Handler");
    }

    /**
     * Handler for ReserveProductCommand that sent through CommandGateway in saga start (order-service)
     *
     * We do not need to execute a separate query to get product details (quantity validation) from the db. Remember that when this aggregate is loaded,
     * it's state will be restored by axon framework for us automatically. Axon framework will create a new object of this aggregate
     * class, and then it will replay the events from the event store to bring this aggregate to the current state. So the quantity
     * filled will be up-to-date, and it will hold the latest value.
     */
    @CommandHandler
    public void handler(ReserveProductCommand reserveProductCommand) /*throws Exception*/ {
        log.info("** CreateProductCommand handler|productId:{},quantity:{}", reserveProductCommand.getProductId(), reserveProductCommand.getQuantity());
        if(this.quantity < reserveProductCommand.getQuantity()){
            throw new IllegalArgumentException("insufficient number of items in the stock!");
        }

        ProductReservedEvent productReservedEvent
                = ProductReservedEvent.builder().orderId(reserveProductCommand.getOrderId())
                                                .userId(reserveProductCommand.getUserId())
                                                .productId(reserveProductCommand.getProductId())
                                                .quantity(reserveProductCommand.getQuantity())
                .build();

        AggregateLifecycle.apply(productReservedEvent);

        /**
         * Axon framework will postpone productReservedEvent and prevent CommandHandler and EventSourcingHandler
         * to do their job if an exception took place even after AggregateLifecycle.apply method
         */
        //if(true) throw new Exception("Something went wrong in Command Handler");
    }

    @CommandHandler
    public void handle(CancelProductReservationCommand cancelProductReservationCommand){
        ProductCancelledReservationEvent productCancelledReservationEvent
                = ProductCancelledReservationEvent.builder().orderId(cancelProductReservationCommand.getOrderId())
                                                    .userId(cancelProductReservationCommand.getUserId())
                                                    .productId(cancelProductReservationCommand.getProductId())
                                                    .quantity(cancelProductReservationCommand.getQuantity())
                                                    .reason(cancelProductReservationCommand.getReason())
                                                .build();
        AggregateLifecycle.apply(productCancelledReservationEvent);
    }

    //EventSourcingHandler from AggregateLifecycle.apply
    @EventSourcingHandler
    public void on(ProductCreatedEvent productCreatedEvent){
        this.productId = productCreatedEvent.getProductId();
        this.title = productCreatedEvent.getTitle();
        this.price = productCreatedEvent.getPrice();
        this.quantity = productCreatedEvent.getQuantity();
    }

    @EventSourcingHandler
    public void on(ProductReservedEvent productReservedEvent){
        log.info("** ProductReservedEvent event source handler|aggregate-quantity:{}|event-quantity:{}", this.quantity, productReservedEvent.getQuantity());
        this.quantity -= productReservedEvent.getQuantity();
        log.info("** ProductReservedEvent event source handler|new-aggregate-quantity:{}|event-quantity:{}");
    }

    @EventSourcingHandler
    public void on (ProductCancelledReservationEvent productCancelReservationEvent){
        log.info("** ProductCancelReservationEvent event source handler|aggregate-quantity:{}|event-quantity:{}", this.quantity, productCancelReservationEvent.getQuantity());
        this.quantity += productCancelReservationEvent.getQuantity();
        log.info("** ProductCancelReservationEvent event source handler|new-aggregate-quantity:{}|event-quantity:{}");
    }
}

