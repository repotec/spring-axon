package com.ahd.ecommerce.demo.read;

import com.ahd.ecommerce.demo.event.ProductAddedEvent;
import com.ahd.ecommerce.demo.event.StockUpdatedEvent;
import com.ahd.ecommerce.demo.read.model.ProductEntity;
import com.ahd.ecommerce.demo.read.query.FindProductQuery;
import com.ahd.ecommerce.demo.read.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductProjector {
    private final ProductRepository productRepository;

    @EventHandler
    public void on(ProductAddedEvent event){
        log.info("ProductAddedEvent handler");
        ProductEntity newProduct = ProductEntity.builder().id(event.getId())
                                                          .price(event.getPrice())
                                                          .stock(event.getStock())
                                                          .description(event.getDescription())
                                                          .build();

        productRepository.save(newProduct);
    }

    @EventHandler
    public void on(StockUpdatedEvent event){
        ProductEntity product = productRepository.findById(event.getId()).orElseThrow(IllegalArgumentException::new);
        product.setStock(product.getStock() - event.getStock());
        productRepository.save(product);
    }

    @QueryHandler
    public List<ProductEntity> handle(FindProductQuery query){
        List<ProductEntity> products = (List<ProductEntity>)productRepository.findAll();
        return products;
    }
}
