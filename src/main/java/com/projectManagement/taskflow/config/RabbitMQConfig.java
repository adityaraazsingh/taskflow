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
    public Declarables rabbitDeclarables() {

        Queue notificationQueue = new Queue("notification.queue", true);

        TopicExchange memberExchange = new TopicExchange("member.exchange");
        TopicExchange taskExchange = new TopicExchange("task.exchange");
        TopicExchange projectExchange = new TopicExchange("project.exchange");
        TopicExchange commentExchange = new TopicExchange("comment.exchange");

        return new Declarables(

                // queue
                notificationQueue,

                // exchanges
                memberExchange,
                taskExchange,
                projectExchange,
                commentExchange,

                // bindings (IMPORTANT)
                BindingBuilder.bind(notificationQueue)
                        .to(memberExchange).with("member.*"),

                BindingBuilder.bind(notificationQueue)
                        .to(taskExchange).with("task.*"),

                BindingBuilder.bind(notificationQueue)
                        .to(projectExchange).with("project.*"),

                BindingBuilder.bind(notificationQueue)
                        .to(commentExchange).with("comment.*")
        );
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