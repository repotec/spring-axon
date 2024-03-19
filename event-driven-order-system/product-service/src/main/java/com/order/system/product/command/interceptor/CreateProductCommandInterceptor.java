package com.order.system.product.command.interceptor;

import com.order.system.product.command.CreateProductCommand;
import com.order.system.product.core.data.ProductLockupEntity;
import com.order.system.product.core.data.ProductLockupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final ProductLockupRepository productLockupRepository;

    /**
     * this DispatchInterceptor will be intercepted after CommandGateway and will be fired before CommandHandler
     */
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends CommandMessage<?>> list) {
        return (index, command) -> {
            if(command.getPayloadType().equals(CreateProductCommand.class)){
                CreateProductCommand createProductCommand = (CreateProductCommand)command.getPayload();
                log.info("Interceptor Command:{}", createProductCommand);

                Optional<ProductLockupEntity> productLockupEntity = productLockupRepository.findByProductIdOrTitle(createProductCommand.getProductId(),
                                                                                                                   createProductCommand.getTitle());
                if(productLockupEntity.isPresent()){
                    throw new IllegalStateException(String.format("Product with productId %s or title %s is already exists",
                                                                  productLockupEntity.get().getProductId(),
                                                                  productLockupEntity.get().getTitle()));
                }
            }
            return command;
        };
    }
}
