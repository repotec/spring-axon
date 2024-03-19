package com.ahd.ecommerce.demo.read.repository;

import com.ahd.ecommerce.demo.read.model.ProductEntity;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductEntity, String> {
}
