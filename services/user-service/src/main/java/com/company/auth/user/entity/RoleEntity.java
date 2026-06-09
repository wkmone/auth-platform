package com.company.auth.user.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@TableName("sys_role")
public class RoleEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;
    private String name;
    private String description;
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
