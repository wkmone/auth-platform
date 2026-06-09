package com.company.auth.user.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class UserCreateRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String email;
    private String phone;
    private String displayName;
    private List<UUID> roleIds;
}
