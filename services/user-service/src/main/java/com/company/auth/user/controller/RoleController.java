package com.company.auth.user.controller;
import com.company.auth.common.model.Result;
import com.company.auth.user.dto.RoleCreateRequest;
import com.company.auth.user.entity.RoleEntity;
import com.company.auth.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    @GetMapping
    public Result<List<RoleEntity>> list() { return Result.success(roleService.list()); }
    @PostMapping
    public Result<RoleEntity> create(@RequestBody RoleCreateRequest req) { return Result.success(roleService.createRole(req)); }
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable UUID id, @RequestBody RoleCreateRequest req) {
        RoleEntity role = roleService.getById(id);
        role.setName(req.getName()); role.setDescription(req.getDescription());
        roleService.updateById(role);
        roleService.assignPermissions(id, req.getPermissionIds());
        return Result.success();
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable UUID id) { roleService.removeById(id); return Result.success(); }
    @PutMapping("/{id}/permissions")
    public Result<Void> assignPermissions(@PathVariable UUID id, @RequestBody List<UUID> permissionIds) { roleService.assignPermissions(id, permissionIds); return Result.success(); }
}
