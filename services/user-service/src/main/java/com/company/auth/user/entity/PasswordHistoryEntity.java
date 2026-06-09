package com.company.auth.user.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@TableName("sys_password_history")
public class PasswordHistoryEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;
    private UUID userId;
    private String passwordHash;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
