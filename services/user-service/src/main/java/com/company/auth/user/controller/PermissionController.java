package com.company.auth.user.controller;
import com.company.auth.common.model.Result;
import com.company.auth.user.entity.PermissionEntity;
import com.company.auth.user.mapper.PermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionMapper permissionMapper;
    @GetMapping
    public Result<List<PermissionEntity>> list() { return Result.success(permissionMapper.selectList(null)); }
}
