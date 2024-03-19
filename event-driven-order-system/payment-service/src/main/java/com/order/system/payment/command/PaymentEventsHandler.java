package com.order.system.payment.command;

import com.order.system.payment.data.Payment;
import com.order.system.payment.data.PaymentRepository;
import com.order.system.core.events.PaymentProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventsHandler {
    private final PaymentRepository paymentRepository;

    @EventHandler
    public void handler(PaymentProcessedEvent paymentProcessedEvent){
        log.info("PaymentProcessedEvent is called for orderId: " + paymentProcessedEvent.getOrderId());

        Payment payment = Payment.builder().paymentId(paymentProcessedEvent.getPaymentId())
                                           .orderId(paymentProcessedEvent.getOrderId())
                                .build();

        paymentRepository.save(payment);
    }
}
