package com.order.system.core.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductCancelledReservationEvent {
    private String productId;
    private String orderId;
    private String userId;
    private Integer quantity;
    private String reason;
}
