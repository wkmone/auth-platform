package com.company.auth.user.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@TableName("sys_login_log")
public class LoginLogEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;
    private UUID userId;
    private String username;
    private String ipAddress;
    private String userAgent;
    private String result;
    private String errorDetail;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
