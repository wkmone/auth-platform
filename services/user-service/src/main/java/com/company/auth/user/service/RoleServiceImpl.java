package com.company.auth.user.service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.auth.user.dto.RoleCreateRequest;
import com.company.auth.user.entity.RoleEntity;
import com.company.auth.user.mapper.RoleMapper;
import com.company.auth.user.mapper.RolePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleEntity> implements RoleService {
    private final RolePermissionMapper rolePermissionMapper;

    @Transactional
    public RoleEntity createRole(RoleCreateRequest req) {
        RoleEntity role = new RoleEntity();
        role.setName(req.getName()); role.setDescription(req.getDescription());
        save(role);
        if (req.getPermissionIds() != null && !req.getPermissionIds().isEmpty())
            assignPermissions(role.getId(), req.getPermissionIds());
        return role;
    }
    @Transactional
    public void assignPermissions(UUID roleId, List<UUID> permissionIds) {
        rolePermissionMapper.deleteByRoleId(roleId);
        if (permissionIds != null && !permissionIds.isEmpty())
            rolePermissionMapper.insertPermissions(roleId, permissionIds);
    }
}
