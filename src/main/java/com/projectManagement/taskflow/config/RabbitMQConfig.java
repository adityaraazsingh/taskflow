package com.projectManagement.taskflow.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public Queue memberNotificationQueue() {
        return new Queue("member.notification.queue");
    }

    @Bean
    public TopicExchange memberExchange() {
        return new TopicExchange("member.exchange");
    }

    @Bean
    public Declarables rabbitDeclarables() {
        Queue memberQueue = new Queue("member.notification.queue");
        TopicExchange memberExchange = new TopicExchange("member.exchange");

        return new Declarables(
                memberQueue,
                memberExchange,
                BindingBuilder.bind(memberQueue).to(memberExchange).with("member.*")
        );
    }


    @Bean
    public Binding memberBinding(
            Queue memberNotificationQueue,
            TopicExchange memberExchange) {

        return BindingBuilder
                .bind(memberNotificationQueue)
                .to(memberExchange)
                .with("member.assigned");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        return factory;
    }
}