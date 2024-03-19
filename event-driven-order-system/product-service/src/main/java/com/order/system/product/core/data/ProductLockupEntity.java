package com.order.system.product.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "product_lockup")
@AllArgsConstructor
@NoArgsConstructor
public class ProductLockupEntity implements Serializable {

    @Id
    private String productId;

    @Column(unique = true)
    private String title;
}
