package com.order.system.product.query;

import com.order.system.product.core.data.Product;
import com.order.system.product.core.data.ProductRepository;
import com.order.system.product.query.rest.ProductRestModel;
import lombok.AllArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

//Projection
//this class could be annotated as service as well
@Component
@AllArgsConstructor
public class ProductQueryHandler {
    private final ProductRepository productRepository;

    /**
     * query handler to query Products from DB and convert into ProductRestModel List
     */
    @QueryHandler
    public List<ProductRestModel> findAllProduct(FindAllProductQuery query){

        List<ProductRestModel> productRestModelList = new ArrayList();
        List<Product> storedProducts = productRepository.findAll();

        storedProducts.forEach(product -> {
                            ProductRestModel productRestModel = new ProductRestModel();
                            BeanUtils.copyProperties(product, productRestModel);
                            productRestModelList.add(productRestModel);
        });

        return productRestModelList;
    }

    @QueryHandler
    public ProductRestModel findProduct(FindProductQuery query){
        List<ProductRestModel> productRestModelList = new ArrayList();
        Product storedProduct = productRepository.findById(query.getProductId()).orElseThrow(IllegalArgumentException::new);

        ProductRestModel productRestModel = new ProductRestModel();
        BeanUtils.copyProperties(storedProduct, productRestModel);

        return productRestModel;
    }
}
