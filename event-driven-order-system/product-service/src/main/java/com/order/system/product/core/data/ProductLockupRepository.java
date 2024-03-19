package com.order.system.product.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductLockupRepository extends JpaRepository<ProductLockupEntity, String> {
    Optional<ProductLockupEntity> findByProductIdOrTitle(String productId, String title);
}
