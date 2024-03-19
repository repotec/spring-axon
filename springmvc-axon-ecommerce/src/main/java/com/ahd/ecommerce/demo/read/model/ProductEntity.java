package com.ahd.ecommerce.demo.read.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductEntity implements Serializable {
    @Id
    private String id;
    private Double price;
    private Integer stock;
    private String description;
}
