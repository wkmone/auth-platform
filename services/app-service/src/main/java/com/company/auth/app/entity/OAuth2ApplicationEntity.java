package com.company.auth.app.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@TableName("oauth2_application")
public class OAuth2ApplicationEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    private String appName;

    private String clientId;

    private String clientSecret;

    private String grantTypes;

    private String redirectUris;

    private String scopes;

    private Integer accessTokenTtl;

    private Boolean requireConsent;

    private String owner;

    private String status;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @Version
    private Integer version;
}
