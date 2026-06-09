package com.company.auth.controller;

import com.company.auth.dto.LoginUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserInfoController {

    @GetMapping("/userinfo")
    public Map<String, Object> userinfo(@AuthenticationPrincipal(expression = "user") LoginUserDetails user) {
        return Map.of(
                "sub", user.getUserId().toString(),
                "username", user.getUsername(),
                "name", user.getDisplayName(),
                "roles", user.getRoles()
        );
    }
}
