package com.company.auth.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppApprovalRequest {

    @NotBlank
    private String reason;
}
