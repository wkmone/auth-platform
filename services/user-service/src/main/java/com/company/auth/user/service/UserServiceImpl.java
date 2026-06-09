package com.company.auth.user.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.auth.common.exception.BusinessException;
import com.company.auth.common.exception.ErrorCode;
import com.company.auth.user.dto.*;
import com.company.auth.user.entity.UserEntity;
import com.company.auth.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
    private final UserMapper userMapper;
    private final PasswordService passwordService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public IPage<UserEntity> pageUsers(UserPageQuery query) {
        return userMapper.pageUsers(new Page<>(query.getPage(), query.getSize()), query.getUsername(), query.getStatus());
    }
    @Transactional
    public UserEntity createUser(UserCreateRequest req) {
        if (lambdaQuery().eq(UserEntity::getUsername, req.getUsername()).exists())
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        passwordService.validatePasswordStrength(req.getPassword());
        UserEntity user = new UserEntity();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setDisplayName(req.getDisplayName());
        user.setStatus("active");
        save(user);
        if (req.getRoleIds() != null && !req.getRoleIds().isEmpty())
            assignRoles(user.getId(), req.getRoleIds());
        passwordService.recordPasswordHistory(user.getId(), user.getPassword());
        return user;
    }
    public void updateUser(UUID id, UserUpdateRequest req) {
        UserEntity user = getById(id);
        if (user == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        user.setEmail(req.getEmail()); user.setPhone(req.getPhone()); user.setDisplayName(req.getDisplayName());
        updateById(user);
    }
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest req) {
        UserEntity user = getById(userId);
        if (user == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword()))
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        passwordService.validatePasswordStrength(req.getNewPassword());
        passwordService.checkPasswordHistory(userId, req.getNewPassword());
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        user.setPwdChangedAt(LocalDateTime.now());
        updateById(user);
        passwordService.recordPasswordHistory(userId, user.getPassword());
    }
    public void updateStatus(UUID id, String status) {
        UserEntity user = getById(id);
        if (user == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        user.setStatus(status);
        if ("active".equals(status)) user.setLockedUntil(null);
        updateById(user);
    }
    @Transactional
    public void assignRoles(UUID userId, List<UUID> roleIds) {
        userMapper.deleteUserRoles(userId);
        if (roleIds != null && !roleIds.isEmpty())
            userMapper.insertUserRoles(userId, roleIds);
    }
    public List<String> findRolesByUserId(UUID userId) { return userMapper.findRolesByUserId(userId); }
    public UserEntity getByUsername(String username) { return lambdaQuery().eq(UserEntity::getUsername, username).one(); }
}
