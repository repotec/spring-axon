package com.order.system.product;

import com.order.system.product.command.interceptor.CreateProductCommandInterceptor;
import com.order.system.product.core.exception.ProductsServiceEventsErrorHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableEurekaClient
public class ProductServiceApplication {
    public static void main( String[] args ) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

    @Autowired
    public void registerCreateProductCommandInterceptor(ApplicationContext applicationContext, CommandBus commandBus){
        commandBus.registerDispatchInterceptor(applicationContext.getBean(CreateProductCommandInterceptor.class));
    }

    /**
     * to register the ListenerInvocationErrorHandler
     *
     * We have two options, We can use our ProductsServiceEventsErrorHandler class or if we don't have our own
     * processing error handler class we can propagatingErrorHandler instead of
     */
    @Autowired
    public void configure(EventProcessingConfigurer eventProcessingConfigurer){
        //option 1:
        eventProcessingConfigurer.registerListenerInvocationErrorHandler("product-group",
                conf -> new ProductsServiceEventsErrorHandler());

        //option 2:
        //eventProcessingConfigurer.registerListenerInvocationErrorHandler("product-group",
        //        conf -> PropagatingErrorHandler.instance();
    }
}
