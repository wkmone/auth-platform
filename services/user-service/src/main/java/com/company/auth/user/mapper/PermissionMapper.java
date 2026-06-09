package com.company.auth.user.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.auth.user.entity.PermissionEntity;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.UUID;

@Mapper
public interface PermissionMapper extends BaseMapper<PermissionEntity> {
    @Select("SELECT p.* FROM sys_permission p JOIN sys_role_permission rp ON p.id = rp.permission_id WHERE rp.role_id = #{roleId} ORDER BY p.sort_order ASC")
    List<PermissionEntity> findByRoleId(@Param("roleId") UUID roleId);
}
