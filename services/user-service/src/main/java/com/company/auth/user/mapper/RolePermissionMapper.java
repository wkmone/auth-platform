package com.company.auth.user.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.auth.user.entity.RolePermissionEntity;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.UUID;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermissionEntity> {
    @Delete("DELETE FROM sys_role_permission WHERE role_id = #{roleId}")
    void deleteByRoleId(@Param("roleId") UUID roleId);

    @Insert("<script>INSERT INTO sys_role_permission (role_id, permission_id) VALUES <foreach collection='permissionIds' item='permissionId' separator=','>(#{roleId}, #{permissionId})</foreach></script>")
    void insertPermissions(@Param("roleId") UUID roleId, @Param("permissionIds") List<UUID> permissionIds);
}
