package com.company.auth.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwksController {

    private final JWKSource<SecurityContext> jwkSource;

    @GetMapping("/oauth2/jwks")
    public Map<String, Object> jwks() {
        try {
            JWKSet jwkSet = new JWKSet(jwkSource.get(null, null));
            return jwkSet.toJSONObject();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve JWK set", e);
        }
    }
}
