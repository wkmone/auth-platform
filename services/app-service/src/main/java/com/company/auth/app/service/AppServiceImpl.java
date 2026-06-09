package com.company.auth.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.auth.app.dto.AppApprovalRequest;
import com.company.auth.app.dto.AppCreateRequest;
import com.company.auth.app.dto.AppUpdateRequest;
import com.company.auth.app.entity.OAuth2ApplicationEntity;
import com.company.auth.app.event.AppEventPublisher;
import com.company.auth.app.mapper.AppMapper;
import com.company.auth.common.exception.BusinessException;
import com.company.auth.common.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppServiceImpl {

    private final AppMapper appMapper;
    private final AppEventPublisher appEventPublisher;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public IPage<OAuth2ApplicationEntity> listApps(int page, int size, String appName, String status) {
        return appMapper.pageApps(new Page<>(page, size), appName, status);
    }

    public OAuth2ApplicationEntity getApp(UUID id) {
        OAuth2ApplicationEntity app = appMapper.selectById(id);
        if (app == null) {
            throw new BusinessException(ErrorCode.APP_NOT_FOUND);
        }
        return app;
    }

    @Transactional
    public Map<String, String> createApp(AppCreateRequest request) {
        // Check app name uniqueness
        LambdaQueryWrapper<OAuth2ApplicationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OAuth2ApplicationEntity::getAppName, request.getAppName());
        if (appMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "应用名称已存在");
        }

        OAuth2ApplicationEntity app = new OAuth2ApplicationEntity();
        app.setAppName(request.getAppName());
        app.setOwner(request.getOwner());
        app.setDescription(request.getDescription());
        app.setStatus("draft");

        // Generate client_id and client_secret
        String clientId = "app_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String rawSecret = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        app.setClientId(clientId);
        app.setClientSecret(passwordEncoder.encode(rawSecret));

        try {
            app.setRedirectUris(request.getRedirectUris() != null
                    ? objectMapper.writeValueAsString(request.getRedirectUris())
                    : "[]");
            app.setScopes(request.getScopes() != null
                    ? objectMapper.writeValueAsString(request.getScopes())
                    : "[\"openid\",\"profile\",\"email\"]");
            app.setGrantTypes(request.getGrantTypes() != null
                    ? objectMapper.writeValueAsString(request.getGrantTypes())
                    : "[\"authorization_code\",\"refresh_token\"]");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize JSON fields", e);
        }

        app.setAccessTokenTtl(request.getAccessTokenTtl() != null ? request.getAccessTokenTtl() : 900);
        app.setRequireConsent(request.getRequireConsent() != null ? request.getRequireConsent() : true);

        appMapper.insert(app);
        log.info("App created: id={}, clientId={}, status=draft", app.getId(), clientId);

        return Map.of(
                "id", app.getId().toString(),
                "clientId", app.getClientId(),
                "clientSecret", rawSecret,
                "status", app.getStatus()
        );
    }

    @Transactional
    public OAuth2ApplicationEntity updateApp(UUID id, AppUpdateRequest request) {
        OAuth2ApplicationEntity app = getApp(id);

        if (request.getAppName() != null) {
            if (!request.getAppName().equals(app.getAppName())) {
                LambdaQueryWrapper<OAuth2ApplicationEntity> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(OAuth2ApplicationEntity::getAppName, request.getAppName());
                if (appMapper.selectCount(wrapper) > 0) {
                    throw new BusinessException(ErrorCode.CONFLICT, "应用名称已存在");
                }
                app.setAppName(request.getAppName());
            }
        }
        if (request.getDescription() != null) app.setDescription(request.getDescription());
        if (request.getAccessTokenTtl() != null) app.setAccessTokenTtl(request.getAccessTokenTtl());
        if (request.getRequireConsent() != null) app.setRequireConsent(request.getRequireConsent());

        try {
            if (request.getRedirectUris() != null)
                app.setRedirectUris(objectMapper.writeValueAsString(request.getRedirectUris()));
            if (request.getScopes() != null)
                app.setScopes(objectMapper.writeValueAsString(request.getScopes()));
            if (request.getGrantTypes() != null)
                app.setGrantTypes(objectMapper.writeValueAsString(request.getGrantTypes()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize JSON fields", e);
        }

        appMapper.updateById(app);
        return app;
    }

    @Transactional
    public void submitForApproval(UUID id) {
        OAuth2ApplicationEntity app = getApp(id);
        if (!"draft".equals(app.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "只有草稿状态的应用可以提交审批");
        }
        // Status stays draft until admin action. The submit action could log an audit event.
        log.info("App submitted for approval: id={}, clientId={}", app.getId(), app.getClientId());
    }

    @Transactional
    public OAuth2ApplicationEntity approveApp(UUID id, AppApprovalRequest request) {
        OAuth2ApplicationEntity app = getApp(id);
        if ("active".equals(app.getStatus())) {
            throw new BusinessException(ErrorCode.APP_ALREADY_APPROVED);
        }
        if (!"draft".equals(app.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "只有草稿状态的应用可以审批");
        }

        app.setStatus("active");
        appMapper.updateById(app);

        // Publish event to Auth Service via RabbitMQ
        appEventPublisher.publishAppApproved(app);

        log.info("App approved: id={}, clientId={}", app.getId(), app.getClientId());
        return app;
    }

    @Transactional
    public void rejectApp(UUID id, AppApprovalRequest request) {
        OAuth2ApplicationEntity app = getApp(id);
        if (!"draft".equals(app.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "只有草稿状态的应用可以驳回");
        }
        app.setStatus("rejected");
        appMapper.updateById(app);
        log.info("App rejected: id={}, clientId={}, reason={}", app.getId(), app.getClientId(), request.getReason());
    }

    @Transactional
    public void updateStatus(UUID id, String targetStatus) {
        OAuth2ApplicationEntity app = getApp(id);
        String currentStatus = app.getStatus();

        switch (targetStatus) {
            case "active" -> {
                if (!"suspended".equals(currentStatus)) {
                    throw new BusinessException(ErrorCode.BAD_REQUEST, "只有已挂起的应用可以恢复");
                }
            }
            case "suspended" -> {
                if (!"active".equals(currentStatus)) {
                    throw new BusinessException(ErrorCode.BAD_REQUEST, "只有已启用的应用可以挂起");
                }
            }
            case "revoked" -> {
                if ("revoked".equals(currentStatus)) {
                    throw new BusinessException(ErrorCode.BAD_REQUEST, "应用已被吊销");
                }
                app.setStatus("revoked");
                appMapper.updateById(app);
                appEventPublisher.publishAppRevoked(app);
                log.info("App revoked: id={}, clientId={}", app.getId(), app.getClientId());
                return;
            }
            default -> throw new BusinessException(ErrorCode.BAD_REQUEST, "无效的状态: " + targetStatus);
        }

        app.setStatus(targetStatus);
        appMapper.updateById(app);
        log.info("App status updated: id={}, status={}->{}", app.getId(), currentStatus, targetStatus);
    }

    @Transactional
    public String rotateSecret(UUID id) {
        OAuth2ApplicationEntity app = getApp(id);
        if ("revoked".equals(app.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "已吊销的应用无法轮换密钥");
        }

        String newSecret = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        app.setClientSecret(passwordEncoder.encode(newSecret));
        appMapper.updateById(app);

        log.info("Secret rotated: id={}, clientId={}", app.getId(), app.getClientId());
        return newSecret;
    }
}
