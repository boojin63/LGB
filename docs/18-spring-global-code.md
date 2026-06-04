# Spring Global Code Template

## 1. Purpose

This document defines common global code templates for Spring Boot projects.

The goal is to keep API responses, exception handling, validation, and shared backend behavior consistent across projects.

## 2. Recommended Global Package Structure

```text
src/main/java/com.LGB/global/
├─ response/
│  └─ ApiResponse.java
│
├─ exception/
│  ├─ GlobalExceptionHandler.java
│  ├─ ErrorCode.java
│  └─ CustomException.java
│
├─ security/
│  ├─ SecurityConfig.java
│  ├─ JwtTokenProvider.java
│  └─ JwtAuthenticationFilter.java
│
└─ config/
   └─ CorsConfig.java
```

## 3. Common API Response Rule

All APIs should return a predictable response format.

### Success

```json
{
  "success": true,
  "data": {},
  "message": null
}
```

### Error

```json
{
  "success": false,
  "data": null,
  "message": "Error message"
}
```

## 4. Exception Handling Rule

Use a global exception handler instead of handling exceptions separately in every controller.

Recommended file:

```text
global/exception/GlobalExceptionHandler.java
```

Common exception handling targets:

- Custom business exceptions
- Validation errors
- Authentication errors
- Authorization errors
- Resource not found errors
- Unexpected server errors

## 5. Error Code Rule

Use a centralized `ErrorCode` enum to manage common errors.

Examples:

| Error Code | HTTP Status | Meaning |
|---|---:|---|
| `INVALID_INPUT` | 400 | Invalid request data |
| `UNAUTHORIZED` | 401 | Login required |
| `FORBIDDEN` | 403 | No permission |
| `NOT_FOUND` | 404 | Resource not found |
| `DUPLICATE_RESOURCE` | 409 | Duplicate data |
| `INTERNAL_SERVER_ERROR` | 500 | Unexpected server error |

## 6. Validation Rule

Request DTOs should use Bean Validation annotations.

Examples:

```java
@NotBlank
@Email
@Size(min = 8)
@NotNull
@Positive
```

Controllers should use `@Valid`.

Example:

```java
@PostMapping
public ApiResponse<ExampleResponse> create(
        @Valid @RequestBody ExampleCreateRequest request
) {
    return ApiResponse.success(exampleService.create(request));
}
```

## 7. Global Code Checklist

- [ ] API response format is consistent.
- [ ] Global exception handling exists.
- [ ] Error codes are centralized.
- [ ] Request DTO validation is used.
- [ ] JPA entities are not returned directly.
- [ ] Sensitive data is excluded from responses.
- [ ] Internal stack traces are not exposed.
- [ ] Error messages are understandable for users.

## 8. Codex Rule

When Codex creates a Spring Boot project, it should:

1. Create or reuse global response structure.
2. Create or reuse global exception structure.
3. Create DTO-based API responses.
4. Avoid returning JPA entities directly.
5. Use `@Valid` for request validation.
6. Explain where global code files should be placed.