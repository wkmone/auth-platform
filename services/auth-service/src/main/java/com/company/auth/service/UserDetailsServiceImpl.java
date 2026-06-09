package com.company.auth.service;

import com.company.auth.dto.LoginUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            String url = "http://localhost:9002/api/users/by-username/" + username;
            @SuppressWarnings("unchecked")
            Map<String, Object> resp = restTemplate.getForObject(url, Map.class);

            if (resp == null || resp.get("data") == null) {
                throw new UsernameNotFoundException("User not found: " + username);
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) resp.get("data");

            return LoginUserDetails.builder()
                    .userId(UUID.fromString((String) user.get("id")))
                    .username((String) user.get("username"))
                    .password((String) user.get("password"))
                    .displayName((String) user.get("displayName"))
                    .enabled("active".equals(user.get("status")))
                    .accountNonLocked(!"locked".equals(user.get("status")))
                    .roles((List<String>) user.getOrDefault("roles", List.of()))
                    .build();
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to load user: {}", username, e);
            throw new UsernameNotFoundException("User service unavailable");
        }
    }
}
