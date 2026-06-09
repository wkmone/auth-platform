package com.company.auth.common.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtDecoder jwtDecoder;

    public Jwt decode(String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (JwtException e) {
            throw new JwtException("Invalid or expired token");
        }
    }

    public String getSubject(String token) {
        return decode(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        return decode(token).getClaimAsStringList("roles");
    }

    public boolean isExpired(String token) {
        try {
            decode(token);
            return false;
        } catch (JwtException e) {
            return true;
        }
    }
}
