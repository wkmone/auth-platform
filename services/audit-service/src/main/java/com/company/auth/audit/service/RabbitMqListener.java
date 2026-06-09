package com.company.auth.audit.service;

import com.company.auth.audit.entity.AuditEventEntity;
import com.company.auth.audit.mapper.AuditEventMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.company.auth.common.constant.AuthConstants.QUEUE_AUDIT_LOG;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMqListener {

    private final AuditEventMapper auditEventMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = QUEUE_AUDIT_LOG)
    public void handleAuditEvent(Map<String, Object> event) {
        try {
            AuditEventEntity entity = new AuditEventEntity();
            entity.setEventType((String) event.get("eventType"));
            entity.setPrincipalId((String) event.get("principalId"));
            entity.setPrincipalName((String) event.get("principalName"));
            entity.setTargetType((String) event.get("targetType"));
            entity.setTargetId((String) event.get("targetId"));
            entity.setResult((String) event.get("result"));
            entity.setErrorDetail((String) event.get("errorDetail"));
            entity.setTraceId((String) event.get("traceId"));

            Object detail = event.get("detail");
            if (detail instanceof Map) {
                entity.setDetail(objectMapper.writeValueAsString(detail));
            } else if (detail instanceof String) {
                entity.setDetail((String) detail);
            }

            auditEventMapper.insert(entity);
            log.debug("Audit event saved: type={}, id={}", entity.getEventType(), entity.getId());
        } catch (Exception e) {
            log.error("Failed to save audit event: {}", event, e);
        }
    }
}
