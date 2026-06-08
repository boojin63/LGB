package com.LGB.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.LGB.domain.auth.dto.LoginRequest;
import com.LGB.domain.auth.dto.LoginResponse;
import com.LGB.domain.user.entity.User;
import com.LGB.domain.user.repository.StudentProfileRepository;
import com.LGB.domain.user.repository.UserRepository;
import com.LGB.global.exception.CustomException;
import com.LGB.global.exception.ErrorCode;
import com.LGB.global.security.JwtTokenProvider;
import com.LGB.global.security.RoleType;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginReturnsTokenAndSafeUserResponse() {
        User user = new User("student@lgb.local", "encoded", "Student", RoleType.STUDENT);
        LoginRequest request = new LoginRequest("student@lgb.local", "password");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPasswordHash())).thenReturn(true);
        when(jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole()))
                .thenReturn("token");
        when(jwtTokenProvider.getExpirationSeconds()).thenReturn(86400L);
        when(studentProfileRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        LoginResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(86400L);
        assertThat(response.user().email()).isEqualTo(user.getEmail());
        assertThat(response.user().role()).isEqualTo(RoleType.STUDENT);
        verify(passwordEncoder).matches(request.password(), user.getPasswordHash());
    }

    @Test
    void loginRejectsInvalidPassword() {
        User user = new User("student@lgb.local", "encoded", "Student", RoleType.STUDENT);
        LoginRequest request = new LoginRequest("student@lgb.local", "wrong");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_CREDENTIALS.getMessage());
    }

    @Test
    void getCurrentUserRejectsMissingUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getCurrentUser(99L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_FOUND.getMessage());
    }
}
