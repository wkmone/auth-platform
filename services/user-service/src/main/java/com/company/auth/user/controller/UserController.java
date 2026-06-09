package com.company.auth.user.controller;
import com.company.auth.common.model.PageResult;
import com.company.auth.common.model.Result;
import com.company.auth.user.dto.*;
import com.company.auth.user.entity.UserEntity;
import com.company.auth.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Result<PageResult<UserEntity>> list(UserPageQuery query) {
        var page = userService.pageUsers(query);
        return Result.success(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }
    @GetMapping("/{id}")
    public Result<UserEntity> getById(@PathVariable UUID id) { return Result.success(userService.getById(id)); }
    @PostMapping("/register")
    public Result<UserEntity> create(@Valid @RequestBody UserCreateRequest req) { return Result.success(userService.createUser(req)); }
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable UUID id, @RequestBody UserUpdateRequest req) { userService.updateUser(id, req); return Result.success(); }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable UUID id) { userService.removeById(id); return Result.success(); }
    @PutMapping("/{id}/password")
    public Result<Void> changePassword(@PathVariable UUID id, @Valid @RequestBody ChangePasswordRequest req) { userService.changePassword(id, req); return Result.success(); }
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) { userService.updateStatus(id, body.get("status")); return Result.success(); }
    @PutMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable UUID id, @RequestBody Map<String, List<UUID>> body) { userService.assignRoles(id, body.get("roleIds")); return Result.success(); }
    @GetMapping("/by-username/{username}")
    public Result<Map<String, Object>> getByUsername(@PathVariable String username) {
        UserEntity user = userService.getByUsername(username);
        if (user == null) return Result.fail(2001, "用户不存在");
        List<String> roles = userService.findRolesByUserId(user.getId());
        return Result.success(Map.of("id", user.getId().toString(), "username", user.getUsername(), "password", user.getPassword(), "displayName", user.getDisplayName(), "email", user.getEmail(), "status", user.getStatus(), "roles", roles));
    }
    @GetMapping("/me")
    public Result<Map<String, Object>> currentUser(@RequestHeader("X-User-Id") String userId) {
        UserEntity user = userService.getById(UUID.fromString(userId));
        if (user == null) return Result.fail(2001, "用户不存在");
        List<String> roles = userService.findRolesByUserId(user.getId());
        return Result.success(Map.of("id", user.getId().toString(), "username", user.getUsername(), "displayName", user.getDisplayName(), "roles", roles));
    }
}
