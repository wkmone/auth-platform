package com.company.auth.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class AppCreateRequest {

    @NotBlank
    private String appName;

    @NotBlank
    private String owner;

    private String description;

    private List<String> redirectUris;

    private List<String> scopes;

    private List<String> grantTypes;

    private Integer accessTokenTtl = 900;

    private Boolean requireConsent = true;
}
