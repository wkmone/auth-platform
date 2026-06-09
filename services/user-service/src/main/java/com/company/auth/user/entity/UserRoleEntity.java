package com.company.auth.user.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.UUID;

@Data
@TableName("sys_user_role")
public class UserRoleEntity {
    private UUID userId;
    private UUID roleId;
}
