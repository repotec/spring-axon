package com.order.system.order.saga;

import com.order.system.core.command.CancelProductReservationCommand;
import com.order.system.core.events.ProductCancelledReservationEvent;
import com.order.system.order.command.ApproveOrderCommand;
import com.order.system.core.command.ProcessPaymentCommand;
import com.order.system.core.command.ReserveProductCommand;
import com.order.system.order.command.RejectOrderCommand;
import com.order.system.order.core.data.OrderStatus;
import com.order.system.order.core.data.OrderSummary;
import com.order.system.order.core.events.OrderApprovedEvent;
import com.order.system.order.core.events.OrderCreatedEvent;
import com.order.system.core.events.PaymentProcessedEvent;
import com.order.system.core.events.ProductReservedEvent;
import com.order.system.core.model.User;
import com.order.system.core.query.FetchUserPaymentDetailsQuery;
import com.order.system.order.core.events.OrderRejectedEvent;
import com.order.system.order.query.FindOrderQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Saga
@Slf4j
public class OrderSaga {
    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    @Autowired
    private transient QueryUpdateEmitter queryUpdateEmitter;

    private final String PAYMENT_PROCESSING_TIMEOUT_DEADLINE = "payment-processing-deadline";

    private String scheduleId;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent){
        log.info("**  OrderCreatedEvent Saga handler for productId:{} and orderId{}:", orderCreatedEvent.getProductId(), orderCreatedEvent.getOrderId());

        ReserveProductCommand reserveProductCommand
                = ReserveProductCommand.builder().orderId(orderCreatedEvent.getOrderId())
                                                 .userId(orderCreatedEvent.getUserId())
                                                 .productId(orderCreatedEvent.getProductId())
                                                 .quantity(orderCreatedEvent.getQuantity())
                                        .build();

        commandGateway.send(reserveProductCommand, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()){
                log.error("**  start compensation transaction from ReserveProductCommand|commandResultMessage", commandResultMessage.exceptionResult());
                RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(orderCreatedEvent.getOrderId(),
                                                                                commandResultMessage.exceptionResult().getMessage());
                commandGateway.send(rejectOrderCommand);
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent){
        log.info("**  ProductReservedEvent Saga handler for productId:{} and orderId{}:", productReservedEvent.getProductId(), productReservedEvent.getOrderId());

        FetchUserPaymentDetailsQuery userPaymentDetailsQuery = new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());
        User userPaymentDetails = null;
        try {
            userPaymentDetails = queryGateway.query(userPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
        }catch (Exception e){
            log.error("**  could not found user payment details from query, start compensation transaction", e.fillInStackTrace());

            cancelProductReservation(productReservedEvent,
                    "could not found user payment details - error occurred while querying from product database|errorMessage:" + e.getMessage());

            return;
        }

        if(userPaymentDetails == null){
            log.info("**  user payment details is null, start compensation transaction");
            cancelProductReservation(productReservedEvent, "user payment details object retrieved but it is null");
            return;
        }

        log.info("**  user payment details has been retrieved successfully for user:{}", userPaymentDetails.getFirstName());

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                                                                            .orderId(productReservedEvent.getOrderId())
                                                                            .paymentDetails(userPaymentDetails.getPaymentDetails())
                                                                            .paymentId(UUID.randomUUID().toString())
                                                                            .build();

        // If the payment processing doesn't complete within 10 seconds, we expect a different event to be triggered.
        // whereas PaymentProcessedEvent get called successfully, then we can cancel this deadline from the same saga class.
        scheduleId = deadlineManager.schedule(Duration.of(10, ChronoUnit.SECONDS),
                                              PAYMENT_PROCESSING_TIMEOUT_DEADLINE,
                                              productReservedEvent);

        if(true) return;

        //processPayment Command to be handled by PaymentAggregate
        String processPaymentCommandResult = null;

        try {
            processPaymentCommandResult = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
        }catch(Exception e){
            log.error("**  start compensation transaction", e.getCause());
            cancelProductReservation(productReservedEvent, "exception occurred while send processPaymentCommand|errorMessage:" + e.getMessage());
            return;
        }

        if(processPaymentCommandResult == null){
            log.error("**  processPaymentCommand result is NULL, start compensation transaction");
            cancelProductReservation(productReservedEvent, "processPaymentCommand has been called and return with null value");
            return;
        }
    }

    //fired by PaymentAggregate
    @SagaEventHandler(associationProperty = "orderId")
    public void handler(PaymentProcessedEvent paymentProcessedEvent){
        log.info("** SagaEventHandler|PaymentProcessedEvent|orderId{}:", paymentProcessedEvent.getOrderId());

        cancelDeadline();
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
        commandGateway.sendAndWait(approveOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handler(OrderApprovedEvent orderApprovedEvent){
        log.info("** Order is Approved, Order Saga is completed for orderId:{}", orderApprovedEvent.getOrderId());

        //emit invocation tells Axon that any clients subscribed to the FindOrderQuery may need to receive an update
        queryUpdateEmitter.emit(FindOrderQuery.class,
                                query -> true,
                                new OrderSummary(orderApprovedEvent.getOrderId(),
                                                 orderApprovedEvent.getOrderStatus(),
                                                null));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handler(ProductCancelledReservationEvent productCancelledReservationEvent){
        log.info("** Order is Cancelled, orderId:{}", productCancelledReservationEvent.getOrderId());

        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(productCancelledReservationEvent.getOrderId(),
                                                                       productCancelledReservationEvent.getReason());

        commandGateway.send(rejectOrderCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void handler(OrderRejectedEvent orderRejectedEvent){
        log.info("** SagaEventHandler|OrderRejectedEvent, orderId:{}", orderRejectedEvent.getOrderId());
        queryUpdateEmitter.emit(FindOrderQuery.class,
                                query -> true,
                                new OrderSummary(orderRejectedEvent.getOrderId(),
                                                 orderRejectedEvent.getOrderStatus(),
                                                 orderRejectedEvent.getReason()));
    }

    @DeadlineHandler(deadlineName = PAYMENT_PROCESSING_TIMEOUT_DEADLINE)
    public void handlePaymentDeadline(ProductReservedEvent productReservedEvent){
        log.info("payment processing deadline took place, send a compensation command to cancel product reservation.");
        cancelProductReservation(productReservedEvent, "payment processing timeout");
    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason){
        cancelDeadline();

        CancelProductReservationCommand cancelProductReservationCommand =
                                                    CancelProductReservationCommand.builder().orderId(productReservedEvent.getOrderId())
                                                    .userId(productReservedEvent.getUserId())
                                                    .productId(productReservedEvent.getProductId())
                                                    .quantity(productReservedEvent.getQuantity())
                                                    .reason(reason)
                                                    .build();

        commandGateway.send(cancelProductReservationCommand);
    }

    //if the payment was processed successfully and if we did receive the payment processed event
    //then there is no need to wait for a deadline anymore.
    private void cancelDeadline(){
        log.info("** cancelDeadline|scheduleId:{}", scheduleId);
        if(scheduleId != null) {
            deadlineManager.cancelSchedule(PAYMENT_PROCESSING_TIMEOUT_DEADLINE, scheduleId);
            scheduleId = null;
        }
    }
}
