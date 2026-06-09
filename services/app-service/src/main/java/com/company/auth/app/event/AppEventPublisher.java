package com.company.auth.app.event;

import com.company.auth.app.entity.OAuth2ApplicationEntity;
import com.company.auth.common.constant.AuthConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void publishAppApproved(OAuth2ApplicationEntity app) {
        try {
            Map<String, Object> event = Map.of(
                    "clientId", app.getClientId(),
                    "clientSecret", app.getClientSecret(),
                    "appName", app.getAppName(),
                    "redirectUris", objectMapper.readValue(app.getRedirectUris(), new TypeReference<List<String>>() {}),
                    "scopes", objectMapper.readValue(app.getScopes(), new TypeReference<List<String>>() {}),
                    "grantTypes", objectMapper.readValue(app.getGrantTypes(), new TypeReference<List<String>>() {})
            );
            rabbitTemplate.convertAndSend(AuthConstants.QUEUE_APP_APPROVED, event);
            log.info("Published app.approved event: clientId={}", app.getClientId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish app approved event", e);
        }
    }

    public void publishAppRevoked(OAuth2ApplicationEntity app) {
        Map<String, Object> event = Map.of("clientId", app.getClientId());
        rabbitTemplate.convertAndSend(AuthConstants.QUEUE_APP_REVOKED, event);
        log.info("Published app.revoked event: clientId={}", app.getClientId());
    }
}
