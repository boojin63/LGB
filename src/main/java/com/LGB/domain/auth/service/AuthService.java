package com.LGB.domain.auth.service;

import com.LGB.domain.auth.dto.LoginRequest;
import com.LGB.domain.auth.dto.LoginResponse;
import com.LGB.domain.auth.dto.MeResponse;
import com.LGB.domain.user.dto.UserResponse;
import com.LGB.domain.user.entity.StudentProfile;
import com.LGB.domain.user.entity.User;
import com.LGB.domain.user.repository.StudentProfileRepository;
import com.LGB.domain.user.repository.UserRepository;
import com.LGB.global.exception.CustomException;
import com.LGB.global.exception.ErrorCode;
import com.LGB.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .filter(User::isActive)
                .filter(found -> passwordEncoder.matches(request.password(), found.getPasswordHash()))
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        String token = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole());
        return new LoginResponse(
                token,
                "Bearer",
                jwtTokenProvider.getExpirationSeconds(),
                toResponse(user)
        );
    }

    public MeResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .filter(User::isActive)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        return MeResponse.from(toResponse(user));
    }

    private UserResponse toResponse(User user) {
        StudentProfile profile = studentProfileRepository.findByUserId(user.getId()).orElse(null);
        return UserResponse.from(user, profile);
    }
}
