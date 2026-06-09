package com.company.auth.notification.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.company.auth.common.constant.AuthConstants.QUEUE_NOTIFY_EMAIL;
import static com.company.auth.common.constant.AuthConstants.QUEUE_NOTIFY_SMS;
import static com.company.auth.common.constant.AuthConstants.QUEUE_NOTIFY_INAPP;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue notifyEmailQueue() { return new Queue(QUEUE_NOTIFY_EMAIL, true); }
    @Bean
    public Queue notifySmsQueue() { return new Queue(QUEUE_NOTIFY_SMS, true); }
    @Bean
    public Queue notifyInappQueue() { return new Queue(QUEUE_NOTIFY_INAPP, true); }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
