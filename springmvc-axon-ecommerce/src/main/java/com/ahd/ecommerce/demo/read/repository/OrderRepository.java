package com.ahd.ecommerce.demo.read.repository;

import com.ahd.ecommerce.demo.read.model.OrderEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OrderRepository extends CrudRepository<OrderEntity, UUID> {
}
