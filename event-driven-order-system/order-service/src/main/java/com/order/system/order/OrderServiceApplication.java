package com.order.system.order;

import org.axonframework.config.Configuration;
import org.axonframework.config.ConfigurationScopeAwareProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.SimpleDeadlineManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class OrderServiceApplication {
    public static void main( String[] args ) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    public DeadlineManager deadlineManager(Configuration configuration, TransactionManager transactionManager){
        return SimpleDeadlineManager.builder().scopeAwareProvider(new ConfigurationScopeAwareProvider(configuration))
                .transactionManager(transactionManager)
                .build();
    }
}
