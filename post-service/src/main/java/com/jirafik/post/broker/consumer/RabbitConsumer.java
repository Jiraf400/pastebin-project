package com.jirafik.post.broker.consumer;

import com.jirafik.post.broker.config.RabbitConfig;
import com.jirafik.post.broker.model.User;
import io.netty.handler.codec.socks.SocksRequestType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitConsumer {

    private String username = "";

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void consumeMessageFromQueue(User user) {
        log.info("Successfully receiver User [{}] from api-gateway", user);
        username = user.getUsername();
    }

    public String getAuthenticatedUserName() {
        return username;
    }
}
