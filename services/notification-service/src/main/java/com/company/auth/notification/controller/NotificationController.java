package com.company.auth.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.auth.common.model.PageResult;
import com.company.auth.common.model.Result;
import com.company.auth.notification.entity.MessageEntity;
import com.company.auth.notification.entity.TemplateEntity;
import com.company.auth.notification.mapper.MessageMapper;
import com.company.auth.notification.mapper.TemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final MessageMapper messageMapper;
    private final TemplateMapper templateMapper;

    @GetMapping("/messages")
    public Result<PageResult<MessageEntity>> listMessages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<MessageEntity> p = new Page<>(page, size);
        IPage<MessageEntity> result = messageMapper.selectPage(p, null);
        return Result.success(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    @GetMapping("/templates")
    public Result<PageResult<TemplateEntity>> listTemplates(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<TemplateEntity> p = new Page<>(page, size);
        IPage<TemplateEntity> result = templateMapper.selectPage(p, null);
        return Result.success(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    @GetMapping("/templates/{id}")
    public Result<TemplateEntity> getTemplate(@PathVariable UUID id) {
        return Result.success(templateMapper.selectById(id));
    }
}
