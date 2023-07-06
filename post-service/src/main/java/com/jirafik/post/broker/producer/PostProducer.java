package com.jirafik.post.broker.producer;

import com.jirafik.post.entity.PostRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.jirafik.post.broker.config.RabbitConfig.*;

@Component
@Slf4j
@AllArgsConstructor
public class PostProducer {

    private final RabbitTemplate template;

    public void sendUploadRequest(PostRequest postRequest) {
        template.convertAndSend(EXCHANGE, ROUTING_KEY_UPLOAD, postRequest);
        log.info("Successfully sent upload request [{}] to post-service", postRequest);
    }

    public void sendDeleteRequest(PostRequest postRequest) {
        template.convertAndSend(EXCHANGE, ROUTING_KEY_DELETE, postRequest);
        log.info("Successfully sent delete request [{}] to post-service", postRequest.getId());
    }

}
