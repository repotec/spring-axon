package com.order.system.order.core.events;

import com.order.system.order.core.data.OrderStatus;
import lombok.Value;

@Value
public class OrderApprovedEvent {
    private final String orderId;
    private final OrderStatus orderStatus = OrderStatus.APPROVED;
}
