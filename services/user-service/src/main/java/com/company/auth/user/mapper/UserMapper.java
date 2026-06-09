package com.company.auth.user.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.auth.user.entity.UserEntity;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.UUID;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
    @Select("SELECT u.* FROM sys_user u WHERE (:username IS NULL OR u.username LIKE CONCAT('%', CAST(:username AS VARCHAR), '%')) AND (:status IS NULL OR u.status = CAST(:status AS VARCHAR)) ORDER BY u.created_at DESC")
    IPage<UserEntity> pageUsers(Page<UserEntity> page, @Param("username") String username, @Param("status") String status);

    @Select("SELECT r.name FROM sys_role r JOIN sys_user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId}")
    List<String> findRolesByUserId(@Param("userId") UUID userId);

    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    void deleteUserRoles(@Param("userId") UUID userId);

    @Insert("<script>INSERT INTO sys_user_role (user_id, role_id) VALUES <foreach collection='roleIds' item='roleId' separator=','>(#{userId}, #{roleId})</foreach></script>")
    void insertUserRoles(@Param("userId") UUID userId, @Param("roleIds") List<UUID> roleIds);
}
