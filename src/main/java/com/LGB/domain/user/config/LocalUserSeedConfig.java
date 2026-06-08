package com.LGB.domain.user.config;

import com.LGB.domain.user.entity.StudentProfile;
import com.LGB.domain.user.entity.User;
import com.LGB.domain.user.repository.StudentProfileRepository;
import com.LGB.domain.user.repository.UserRepository;
import com.LGB.global.security.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalUserSeedConfig implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "admin@lgb.local";
    private static final String STUDENT_EMAIL = "student@lgb.local";

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        createUserIfMissing(ADMIN_EMAIL, "admin1234!", "Local Admin", RoleType.ADMIN);

        User student = createUserIfMissing(
                STUDENT_EMAIL,
                "student1234!",
                "Local Student",
                RoleType.STUDENT
        );

        if (studentProfileRepository.findByUserId(student.getId()).isEmpty()) {
            studentProfileRepository.save(new StudentProfile(student, "LGB-LOCAL-001", "LGB", 1));
        }
    }

    private User createUserIfMissing(String email, String password, String name, RoleType role) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        new User(email, passwordEncoder.encode(password), name, role)
                ));
    }
}
