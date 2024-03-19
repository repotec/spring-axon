package com.ahd.ecommerce.demo.read.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderEntity implements Serializable {
    @Id
    private UUID orderId;
    private Double price;
    private Integer quantity;
    @Column(name = "product_id")
    private String productId;

    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private ProductEntity product;
}
