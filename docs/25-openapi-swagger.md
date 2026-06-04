# OpenAPI / Swagger Template

## 1. Purpose

This document defines the API documentation standard for Spring Boot projects using springdoc-openapi.

The goal is to make backend APIs easier to understand, test, and share.

springdoc-openapi automatically generates OpenAPI documentation by inspecting Spring application configuration, class structure, and annotations.

## 2. Why Use OpenAPI

OpenAPI helps with:

- API documentation
- API testing
- Frontend-backend collaboration
- Request/response verification
- Reducing outdated API documents

## 3. Recommended Dependency

Gradle:

```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9'
```

Maven:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.9</version>
</dependency>
```

If the version becomes outdated, check the official springdoc-openapi documentation before applying.

## 4. Default URLs

Common URLs:

```text
Swagger UI:
http://localhost:8080/swagger-ui/index.html

OpenAPI JSON:
http://localhost:8080/v3/api-docs
```

## 5. Recommended Config File

Copy this file:

```text
config-templates/backend/openapi/OpenApiConfig.java
```

To actual project:

```text
src/main/java/com.LGB/global/config/OpenApiConfig.java
```

Then update:

- package name
- title
- description
- version
- contact
- server URL

## 6. Controller Documentation Example

```java
@Operation(summary = "게시글 목록 조회", description = "게시글 목록을 조회합니다.")
@GetMapping("/api/posts")
public ApiResponse<List<PostResponse>> findAll() {
    return ApiResponse.success(postService.findAll());
}
```

## 7. DTO Documentation Example

```java
@Schema(description = "게시글 생성 요청")
public record PostCreateRequest(

        @Schema(description = "제목", example = "첫 번째 게시글")
        @NotBlank
        String title,

        @Schema(description = "내용", example = "게시글 내용입니다.")
        @NotBlank
        String content
) {
}
```

## 8. Security Note

Swagger UI should be handled carefully in production.

Options:

1. Allow only in local/dev profile.
2. Protect Swagger UI with authentication.
3. Disable in production if not needed.

## 9. Checklist

- [ ] springdoc dependency added.
- [ ] Swagger UI works locally.
- [ ] OpenAPI JSON works locally.
- [ ] API title and version are configured.
- [ ] Important APIs have descriptions.
- [ ] Request DTOs are documented.
- [ ] Production exposure is reviewed.
- [ ] Security endpoints are not confusing or exposed carelessly.

## 10. Codex Rule

When Codex adds OpenAPI:

1. Add dependency only if missing.
2. Add config file if useful.
3. Document important APIs.
4. Avoid over-documenting simple internal code.
5. Check whether Swagger should be available in production.