package com.company.auth.user.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.UUID;

@Data
@TableName("sys_role_permission")
public class RolePermissionEntity {
    private UUID roleId;
    private UUID permissionId;
}
