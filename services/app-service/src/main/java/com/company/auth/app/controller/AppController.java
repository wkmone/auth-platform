package com.company.auth.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.company.auth.app.dto.AppApprovalRequest;
import com.company.auth.app.dto.AppCreateRequest;
import com.company.auth.app.dto.AppUpdateRequest;
import com.company.auth.app.entity.OAuth2ApplicationEntity;
import com.company.auth.app.service.AppServiceImpl;
import com.company.auth.common.model.PageResult;
import com.company.auth.common.model.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/apps")
@RequiredArgsConstructor
public class AppController {

    private final AppServiceImpl appService;

    @GetMapping
    public Result<PageResult<OAuth2ApplicationEntity>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String appName,
            @RequestParam(required = false) String status) {
        IPage<OAuth2ApplicationEntity> result = appService.listApps(page, size, appName, status);
        result.getRecords().forEach(app -> app.setClientSecret(null));
        return Result.success(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<OAuth2ApplicationEntity> get(@PathVariable UUID id) {
        OAuth2ApplicationEntity app = appService.getApp(id);
        app.setClientSecret(null);
        return Result.success(app);
    }

    @PostMapping
    public Result<Map<String, String>> create(@Valid @RequestBody AppCreateRequest request) {
        return Result.success(appService.createApp(request));
    }

    @PutMapping("/{id}")
    public Result<OAuth2ApplicationEntity> update(@PathVariable UUID id, @Valid @RequestBody AppUpdateRequest request) {
        return Result.success(appService.updateApp(id, request));
    }

    @PostMapping("/{id}/submit")
    public Result<Void> submit(@PathVariable UUID id) {
        appService.submitForApproval(id);
        return Result.success();
    }

    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable UUID id, @RequestBody(required = false) AppApprovalRequest request) {
        appService.approveApp(id, request != null ? request : new AppApprovalRequest());
        return Result.success();
    }

    @PostMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable UUID id, @RequestBody(required = false) AppApprovalRequest request) {
        appService.rejectApp(id, request != null ? request : new AppApprovalRequest());
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable UUID id, @RequestParam String status) {
        appService.updateStatus(id, status);
        return Result.success();
    }

    @PostMapping("/{id}/secret/rotate")
    public Result<Map<String, String>> rotateSecret(@PathVariable UUID id) {
        String newSecret = appService.rotateSecret(id);
        return Result.success(Map.of("clientSecret", newSecret));
    }
}
