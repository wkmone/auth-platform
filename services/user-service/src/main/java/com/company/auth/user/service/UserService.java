package com.company.auth.user.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.company.auth.user.dto.*;
import com.company.auth.user.entity.UserEntity;
import java.util.List;
import java.util.UUID;

public interface UserService extends IService<UserEntity> {
    IPage<UserEntity> pageUsers(UserPageQuery query);
    UserEntity createUser(UserCreateRequest req);
    void updateUser(UUID id, UserUpdateRequest req);
    void changePassword(UUID userId, ChangePasswordRequest req);
    void updateStatus(UUID id, String status);
    void assignRoles(UUID userId, List<UUID> roleIds);
    List<String> findRolesByUserId(UUID userId);
    UserEntity getByUsername(String username);
}
