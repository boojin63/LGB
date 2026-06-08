package com.LGB.domain.user.dto;

import com.LGB.domain.user.entity.StudentProfile;

public record StudentProfileResponse(
        String studentNumber,
        String department,
        int grade
) {
    public static StudentProfileResponse from(StudentProfile profile) {
        return new StudentProfileResponse(
                profile.getStudentNumber(),
                profile.getDepartment(),
                profile.getGrade()
        );
    }
}
