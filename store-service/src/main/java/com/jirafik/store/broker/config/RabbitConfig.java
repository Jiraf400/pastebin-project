package com.jirafik.store.broker.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "pastebin:post:exchange";
    public static final String QUEUE_UPLOAD = "pastebin:post:queue:upload";
    public static final String QUEUE_DELETE = "pastebin:post:queue:delete";
    public static final String ROUTING_KEY_UPLOAD = "pastebin:post:routing:key:upload";
    public static final String ROUTING_KEY_DELETE = "pastebin:post:routing:key:delete";

    @Bean
    public Queue queueUpload() {
        return new Queue(QUEUE_UPLOAD);
    }

    @Bean
    public Queue queueDelete() {
        return new Queue(QUEUE_DELETE);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public List<Binding> binding() {
        return Arrays.asList(
                BindingBuilder.bind(queueUpload()).to(exchange()).with(ROUTING_KEY_UPLOAD),
                BindingBuilder.bind(queueDelete()).to(exchange()).with(ROUTING_KEY_DELETE));
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

}
