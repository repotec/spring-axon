package com.order.system.product.command;

import com.order.system.product.core.data.ProductLockupEntity;
import com.order.system.product.core.data.ProductLockupRepository;
import com.order.system.product.core.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
@RequiredArgsConstructor
public class ProductLockupEventsHandler {
    private final ProductLockupRepository productLockupRepository;

    /**
     * for set based consistency validation
     * ************************************
     * Command model to validate if product exists ot not, however there is a separation between command and query model
     *
     * problem:
     * *******
     * to validate if the product exists or not, A logical first assumption would be to use a query model from our
     * application to solve our problem. However, as CQRS dictates, the synchronization between both models takes some
     * time and introduces eventual consistency. Mainly if events are used to drive both models, we will be held back
     * by this. The query model cannot guarantee it knows the entire set due to this, and as such, it cannot be our
     * solution to the problem.
     *
     * solution:
     * ********
     * So we should look further into our command model. It does not necessarily exist solely from aggregates.
     * A command model can contain any form of the data model. Thus, to solve the set-based consistency validation
     * for existence of product, we can introduce a small product look-up table. It is this model which can be consulted
     * during this process to allow for this form of validation. To ensure this other model deals with the problem correctly,
     * any changes occurring on the model should be immediately consistent with the rest.
     *
     * it will not be exposed by the Query API.
     * the client application that communicates with the Query API, it will not be able to query this look up table.
     * it will be used only in the command model
     *
     * flow:
     * ****
     * Create Product Endpoint
     *                      fire CreateProductCommand
     *                                --> @CommandHandler
     *                                             --> fire ProductCreatedEvent
     *                                                          --> @EventHandler
     *                                                                       --> persist into product Lock-up table
     *  product will be validated through CreateProductCommandInterceptor (CommandInterceptor)
     */
    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent){
        ProductLockupEntity productLockupEntity = new ProductLockupEntity(productCreatedEvent.getProductId(),
                                                                          productCreatedEvent.getTitle());
        productLockupRepository.save(productLockupEntity);
    }
}
