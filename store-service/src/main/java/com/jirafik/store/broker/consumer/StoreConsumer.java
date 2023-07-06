package com.jirafik.store.broker.consumer;

import com.jirafik.store.broker.config.RabbitConfig;
import com.jirafik.store.dto.PostRequest;
import com.jirafik.store.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.springframework.amqp.core.ExchangeTypes.TOPIC;

@Component
@Slf4j
public class StoreConsumer {

    @Autowired
    private StoreService service;
    private PostRequest postRequest;
    private String postId;

    @RabbitListener(bindings = @QueueBinding(key = RabbitConfig.ROUTING_KEY_UPLOAD, value =
    @Queue(value = RabbitConfig.QUEUE_UPLOAD), exchange = @Exchange(value = "pastebin:post:exchange", type = TOPIC)))
    public void consumeUploadMessageFromQueue(PostRequest post) {
        postRequest = post;
        service.uploadData(getPostRequest());
        log.info("Successfully receiver post body [{}] from api-gateway", post);
    }

    @RabbitListener(bindings = @QueueBinding(key = RabbitConfig.ROUTING_KEY_DELETE, value =
    @Queue(value = RabbitConfig.QUEUE_DELETE), exchange = @Exchange(value = "pastebin:post:exchange", type = TOPIC)))
    public void consumeDeleteMessageFromQueue(PostRequest postRequest) {
        postId = postRequest.getId();
        service.deleteData(getPostId());
        log.info("Successfully receiver Post id [{}]", postRequest.getId());
    }

    public PostRequest getPostRequest() {
        return postRequest;
    }

    public String getPostId() {
        return postId;
    }


}
