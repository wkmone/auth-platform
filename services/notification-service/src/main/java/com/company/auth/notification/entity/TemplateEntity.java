package com.company.auth.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@TableName("notification_template")
public class TemplateEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;
    private String code;
    private String name;
    private String channel;
    private String subjectTemplate;
    private String bodyTemplate;
    private String variables;
    private Boolean enabled;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
