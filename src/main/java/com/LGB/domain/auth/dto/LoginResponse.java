package com.LGB.domain.auth.dto;

import com.LGB.domain.user.dto.UserResponse;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserResponse user
) {
}
