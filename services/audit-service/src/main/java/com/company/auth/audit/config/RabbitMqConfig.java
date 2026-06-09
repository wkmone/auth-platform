package com.company.auth.audit.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.company.auth.common.constant.AuthConstants.QUEUE_AUDIT_LOG;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue auditLogQueue() {
        return new Queue(QUEUE_AUDIT_LOG, true);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
