package com.order.system.product.query.rest;

import com.order.system.product.query.FindAllProductQuery;
import com.order.system.product.query.FindProductQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/products")
public class ProductQueryController {
    @Autowired
    QueryGateway queryGateway;

    @GetMapping
    public List<ProductRestModel> getProducts(){
        FindAllProductQuery findAllProductQuery = new FindAllProductQuery();
        List<ProductRestModel>  products = queryGateway.query(findAllProductQuery,
                                                              ResponseTypes.multipleInstancesOf(ProductRestModel.class))
                                                       .join();

        return products;
    }

    @GetMapping("/{id}")
    public ProductRestModel getProductById(@PathVariable("id") String productId){
        FindProductQuery findProductQuery = new FindProductQuery(productId);
        ProductRestModel  products = queryGateway.query(findProductQuery,ProductRestModel.class).join();
        return products;
    }
}
