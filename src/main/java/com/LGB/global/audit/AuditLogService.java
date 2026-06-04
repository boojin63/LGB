package com.LGB.global.audit;

import com.LGB.global.logging.LoggingConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void record(
            Long actorId,
            String actorRole,
            String action,
            String targetType,
            Long targetId,
            HttpServletRequest request
    ) {
        String requestId = MDC.get(LoggingConstants.REQUEST_ID_MDC_KEY);
        String ipAddress = extractClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        AuditLog auditLog = new AuditLog(
                actorId,
                actorRole,
                action,
                targetType,
                targetId,
                requestId,
                ipAddress,
                userAgent
        );

        auditLogRepository.save(auditLog);
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}