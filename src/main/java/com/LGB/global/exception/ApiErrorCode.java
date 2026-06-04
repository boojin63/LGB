package com.LGB.global.exception;

import org.springframework.http.HttpStatus;

public enum ApiErrorCode {

    COMMON_INVALID_REQUEST("COMMON_001", HttpStatus.BAD_REQUEST, "Invalid request."),
    COMMON_VALIDATION_FAILED("COMMON_002", HttpStatus.BAD_REQUEST, "Validation failed."),
    COMMON_RESOURCE_NOT_FOUND("COMMON_003", HttpStatus.NOT_FOUND, "Resource not found."),
    COMMON_DUPLICATE_RESOURCE("COMMON_004", HttpStatus.CONFLICT, "Resource already exists."),
    COMMON_INTERNAL_SERVER_ERROR("COMMON_999", HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error."),

    AUTH_REQUIRED("AUTH_001", HttpStatus.UNAUTHORIZED, "Authentication is required."),
    AUTH_INVALID_TOKEN("AUTH_002", HttpStatus.UNAUTHORIZED, "Invalid token."),
    AUTH_EXPIRED_TOKEN("AUTH_003", HttpStatus.UNAUTHORIZED, "Token has expired."),
    AUTH_ACCESS_DENIED("AUTH_004", HttpStatus.FORBIDDEN, "Access denied."),
    AUTH_INVALID_CREDENTIALS("AUTH_005", HttpStatus.BAD_REQUEST, "Invalid login credentials."),

    USER_NOT_FOUND("USER_001", HttpStatus.NOT_FOUND, "User not found."),
    USER_EMAIL_DUPLICATED("USER_002", HttpStatus.CONFLICT, "Email already exists."),
    USER_INVALID_STATUS("USER_003", HttpStatus.BAD_REQUEST, "Invalid user status."),
    USER_FORBIDDEN_PRIVATE_DATA("USER_004", HttpStatus.FORBIDDEN, "Cannot access another user's data.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ApiErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}