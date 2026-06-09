package com.company.auth.common.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static com.company.auth.common.constant.AuthConstants.QUEUE_AUDIT_LOG;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(String eventType, UUID principalId, String principalName,
                        String targetType, String targetId, Map<String, Object> detail,
                        String result, String errorDetail) {
        Map<String, Object> event = Map.of(
                "eventType", eventType,
                "principalId", principalId != null ? principalId.toString() : null,
                "principalName", principalName,
                "targetType", targetType,
                "targetId", targetId,
                "detail", detail != null ? detail : Map.of(),
                "result", result,
                "errorDetail", errorDetail != null ? errorDetail : "",
                "timestamp", LocalDateTime.now().toString()
        );
        rabbitTemplate.convertAndSend(QUEUE_AUDIT_LOG, event);
        log.debug("Audit event published: type={}, principal={}, target={}", eventType, principalName, targetId);
    }
}
