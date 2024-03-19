package com.order.system.order.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Order {
    @Id
    @Column(unique = true)
    public String orderId;
    private String userId;
    private String productId;
    private int quantity;
    private String addressId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
