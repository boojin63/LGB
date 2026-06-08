package com.LGB.domain.user.dto;

import com.LGB.domain.user.entity.StudentProfile;
import com.LGB.domain.user.entity.User;
import com.LGB.global.security.RoleType;

public record UserResponse(
        Long id,
        String email,
        String name,
        RoleType role,
        StudentProfileResponse studentProfile
) {
    public static UserResponse from(User user, StudentProfile profile) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                profile == null ? null : StudentProfileResponse.from(profile)
        );
    }
}
