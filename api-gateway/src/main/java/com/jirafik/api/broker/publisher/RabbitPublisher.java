package com.jirafik.api.broker.publisher;

import com.jirafik.api.broker.config.RabbitConfig;
import com.jirafik.api.broker.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Component
@Slf4j
@AllArgsConstructor
public class RabbitPublisher {

    private final RabbitTemplate template;

    public void sendUser(User user) {
        template.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, user);
        log.info("Successfully sent username [{}] to post-service", user);
    }
}
