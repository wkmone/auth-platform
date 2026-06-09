package com.company.auth.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class AppUpdateRequest {

    private String appName;

    private String description;

    private List<String> redirectUris;

    private List<String> scopes;

    private List<String> grantTypes;

    private Integer accessTokenTtl;

    private Boolean requireConsent;
}
