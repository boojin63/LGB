package com.LGB.domain.auth.dto;

import com.LGB.domain.user.dto.StudentProfileResponse;
import com.LGB.domain.user.dto.UserResponse;
import com.LGB.global.security.RoleType;

public record MeResponse(
        Long id,
        String email,
        String name,
        RoleType role,
        StudentProfileResponse studentProfile
) {
    public static MeResponse from(UserResponse user) {
        return new MeResponse(
                user.id(),
                user.email(),
                user.name(),
                user.role(),
                user.studentProfile()
        );
    }
}
