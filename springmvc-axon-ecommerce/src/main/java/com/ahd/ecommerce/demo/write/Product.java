package com.ahd.ecommerce.demo.write;

import com.ahd.ecommerce.demo.command.AddProductCommand;
import com.ahd.ecommerce.demo.command.UpdateStockCommand;
import com.ahd.ecommerce.demo.event.ProductAddedEvent;
import com.ahd.ecommerce.demo.event.StockUpdatedEvent;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
@Data
public class Product {
    @AggregateIdentifier
    private String id;
    private Double price;
    private Integer stock;
    private String description;

    @CommandHandler
    public Product(AddProductCommand cmd){
        AggregateLifecycle.apply(ProductAddedEvent.builder().id(cmd.getId())
                                                            .price(cmd.getPrice())
                                                            .stock(cmd.getStock())
                                                            .description(cmd.getDescription())
                                                   .build());
    }

    @EventSourcingHandler
    public void on(ProductAddedEvent event){
        this.id = event.getId();
        this.stock = event.getStock();
        this.price = event.getPrice();
        this.description = event.getDescription();
    }

    @CommandHandler
    public void handleUpdateStockCommand(UpdateStockCommand cmd){
        if(this.stock >= cmd.getStock()) {
            AggregateLifecycle.apply(StockUpdatedEvent.builder().id(cmd.getId())
                    .stock(cmd.getStock())
                    .build());
        }else{
            throw new RuntimeException("no available stock for this product");
        }
    }

    @EventSourcingHandler
    public void on(StockUpdatedEvent event){
        this.id = event.getId();
        this.stock -= event.getStock();
    }
}