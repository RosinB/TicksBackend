package com.example.demo.common.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTemplateConfig {

    @Autowired
    public void configureRabbitTemplate(RabbitTemplate rabbitTemplate, Jackson2JsonMessageConverter messageConverter) {
        rabbitTemplate.setMessageConverter(messageConverter);
    }
}