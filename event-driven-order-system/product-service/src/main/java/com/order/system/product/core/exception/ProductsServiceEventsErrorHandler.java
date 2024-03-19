package com.order.system.product.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.EventMessageHandler;
import org.axonframework.eventhandling.ListenerInvocationErrorHandler;

import javax.annotation.Nonnull;

/**
 * ListenerInvocationErrorHandler class
 * solution 2: outside the ProductEventHandler class
 */
@Slf4j
public class ProductsServiceEventsErrorHandler implements ListenerInvocationErrorHandler {
    @Override
    public void onError(@Nonnull Exception exception,
                        @Nonnull EventMessage<?> eventMessage,
                        @Nonnull EventMessageHandler eventMessageHandler) throws Exception {

        log.error("error caught by ProductsServiceEventsErrorHandler", exception);

        throw exception;
    }
}
