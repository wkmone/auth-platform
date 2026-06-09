package com.company.auth.user.service;
import com.company.auth.common.exception.BusinessException;
import com.company.auth.common.exception.ErrorCode;
import com.company.auth.user.entity.PasswordHistoryEntity;
import com.company.auth.user.mapper.PasswordHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    private final PasswordHistoryMapper passwordHistoryMapper;
    private static final Pattern PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{10,}$");

    public void validatePasswordStrength(String password) {
        if (!PATTERN.matcher(password).matches())
            throw new BusinessException(ErrorCode.PASSWORD_TOO_WEAK, "密码至少10位，需包含大写字母、小写字母、数字和特殊字符");
    }
    public void checkPasswordHistory(UUID userId, String newPassword) {
        List<PasswordHistoryEntity> history = passwordHistoryMapper.findTop5ByUserId(userId);
        for (PasswordHistoryEntity h : history)
            if (passwordEncoder.matches(newPassword, h.getPasswordHash()))
                throw new BusinessException(ErrorCode.PASSWORD_REUSED);
    }
    public void recordPasswordHistory(UUID userId, String hashedPassword) {
        PasswordHistoryEntity h = new PasswordHistoryEntity();
        h.setUserId(userId); h.setPasswordHash(hashedPassword);
        passwordHistoryMapper.insert(h);
    }
}
