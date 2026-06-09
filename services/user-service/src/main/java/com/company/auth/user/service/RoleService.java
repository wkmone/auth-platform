package com.company.auth.user.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.company.auth.user.dto.RoleCreateRequest;
import com.company.auth.user.entity.RoleEntity;
import java.util.List;
import java.util.UUID;

public interface RoleService extends IService<RoleEntity> {
    RoleEntity createRole(RoleCreateRequest req);
    void assignPermissions(UUID roleId, List<UUID> permissionIds);
}
