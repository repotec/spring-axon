package com.order.system.user.core.query;

import com.order.system.core.model.PaymentDetails;
import com.order.system.core.model.User;
import com.order.system.core.query.FetchUserPaymentDetailsQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserEventsHandler {
    //will be triggered through QueryGateway.query in saga @SagaEventHandler(associationProperty = "orderId")
    //while handling ProductReserved Event
    @QueryHandler
    public User findUserPaymentDetails(FetchUserPaymentDetailsQuery query) {
        log.info("QueryHandler|FetchUserPaymentDetailsQuery|userId:{}", query.getUserId());

        //simulate find from repository
        PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("1234-1234-1234-1234")
                .cvv("123")
                .name("AHMED MOHAMMED")
                .validUntilMonth(12)
                .validUntilYear(2030)
                .build();

        User user = User.builder()
                .firstName("Ahmed")
                .lastName("Mohammed")
                .userId(query.getUserId())
                .paymentDetails(paymentDetails)
                .build();

        return user;
    }
}
