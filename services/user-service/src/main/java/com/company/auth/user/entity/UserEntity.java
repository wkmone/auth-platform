package com.company.auth.user.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@TableName("sys_user")
public class UserEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String displayName;
    private String status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime lockedUntil;
    private LocalDateTime pwdChangedAt;
    private Boolean mfaEnabled;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
