package com.company.auth.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@TableName("notification_message")
public class MessageEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;
    private UUID templateId;
    private String recipient;
    private String channel;
    private String subject;
    private String body;
    private String status;
    private Integer retryCount;
    private Integer maxRetries;
    private String errorDetail;
    private LocalDateTime sentAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
