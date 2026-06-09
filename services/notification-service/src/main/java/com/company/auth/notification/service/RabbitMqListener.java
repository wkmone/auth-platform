package com.company.auth.notification.service;

import com.company.auth.notification.entity.MessageEntity;
import com.company.auth.notification.entity.TemplateEntity;
import com.company.auth.notification.mapper.MessageMapper;
import com.company.auth.notification.mapper.TemplateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static com.company.auth.common.constant.AuthConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMqListener {

    private final TemplateMapper templateMapper;
    private final MessageMapper messageMapper;
    private final EmailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    @RabbitListener(queues = QUEUE_NOTIFY_EMAIL)
    public void handleEmailNotification(Map<String, Object> event) {
        processNotification(event, "email");
    }

    @RabbitListener(queues = QUEUE_NOTIFY_SMS)
    public void handleSmsNotification(Map<String, Object> event) {
        processNotification(event, "sms");
    }

    @RabbitListener(queues = QUEUE_NOTIFY_INAPP)
    public void handleInAppNotification(Map<String, Object> event) {
        processNotification(event, "inapp");
    }

    private void processNotification(Map<String, Object> event, String defaultChannel) {
        String templateCode = (String) event.get("templateCode");
        String recipient = (String) event.get("recipient");
        String channel = event.getOrDefault("channel", defaultChannel).toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>) event.getOrDefault("variables", Map.of());

        if (templateCode == null || recipient == null) {
            log.error("Missing templateCode or recipient in notification event: {}", event);
            return;
        }

        TemplateEntity template = templateMapper.findByCode(templateCode);
        if (template == null) {
            log.error("Template not found or disabled: {}", templateCode);
            return;
        }

        // Render template
        Context context = new Context();
        variables.forEach(context::setVariable);
        String subject = templateEngine.process(template.getSubjectTemplate(), context);
        String body = templateEngine.process(template.getBodyTemplate(), context);

        // Create message record
        MessageEntity message = new MessageEntity();
        message.setTemplateId(template.getId());
        message.setRecipient(recipient);
        message.setChannel(channel);
        message.setSubject(subject);
        message.setBody(body);
        message.setStatus("pending");
        message.setRetryCount(0);
        message.setMaxRetries(3);
        messageMapper.insert(message);

        // Send via channel
        try {
            switch (channel) {
                case "email" -> {
                    emailSender.send(recipient, subject, body);
                    message.setStatus("sent");
                    message.setSentAt(LocalDateTime.now());
                }
                case "sms" -> {
                    log.info("SMS to {}: {}", recipient, body);
                    message.setStatus("sent");
                    message.setSentAt(LocalDateTime.now());
                }
                case "inapp" -> {
                    log.info("In-app message to {}: {}", recipient, subject);
                    message.setStatus("sent");
                    message.setSentAt(LocalDateTime.now());
                }
            }
        } catch (Exception e) {
            log.error("Failed to send notification: channel={}, recipient={}", channel, recipient, e);
            message.setStatus("failed");
            message.setErrorDetail(e.getMessage());
        }

        messageMapper.updateById(message);
    }
}
