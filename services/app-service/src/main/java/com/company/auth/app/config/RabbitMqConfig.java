package com.company.auth.app.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.company.auth.common.constant.AuthConstants.QUEUE_APP_APPROVED;
import static com.company.auth.common.constant.AuthConstants.QUEUE_APP_REVOKED;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue appApprovedQueue() {
        return new Queue(QUEUE_APP_APPROVED, true);
    }

    @Bean
    public Queue appRevokedQueue() {
        return new Queue(QUEUE_APP_REVOKED, true);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
