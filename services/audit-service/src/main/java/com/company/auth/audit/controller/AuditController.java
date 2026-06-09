package com.company.auth.audit.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.auth.audit.entity.AuditEventEntity;
import com.company.auth.audit.mapper.AuditEventMapper;
import com.company.auth.common.model.PageResult;
import com.company.auth.common.model.Result;
import com.company.auth.common.exception.BusinessException;
import com.company.auth.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditEventMapper auditEventMapper;

    @GetMapping("/events")
    public Result<PageResult<AuditEventEntity>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String principalName,
            @RequestParam(required = false) String result) {
        IPage<AuditEventEntity> resultPage = auditEventMapper.pageEvents(
                new Page<>(page, size),
                eventType, principalName, result);
        return Result.success(PageResult.of(resultPage.getTotal(), resultPage.getCurrent(),
                resultPage.getSize(), resultPage.getRecords()));
    }

    @GetMapping("/events/{id}")
    public Result<AuditEventEntity> get(@PathVariable UUID id) {
        AuditEventEntity event = auditEventMapper.selectById(id);
        if (event == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "审计事件不存在");
        }
        return Result.success(event);
    }
}
