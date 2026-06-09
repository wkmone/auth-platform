package com.company.auth.user.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@TableName("sys_permission")
public class PermissionEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;
    private String code;
    private String type;
    private String path;
    private String method;
    private UUID parentId;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
