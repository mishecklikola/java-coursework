package com.example.demo.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "task.events";
    public static final String QUEUE = "task.events.queue";
    public static final String ROUTING_PATTERN = "task.*";

    @Bean
    public TopicExchange taskExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue taskQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public Binding taskBinding(Queue taskQueue, TopicExchange taskExchange) {
        return BindingBuilder.bind(taskQueue).to(taskExchange).with(ROUTING_PATTERN);
    }

    @Bean
    public MessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter converter) {
        RabbitTemplate tpl = new RabbitTemplate(cf);
        tpl.setMessageConverter(converter);
        return tpl;
    }
}
