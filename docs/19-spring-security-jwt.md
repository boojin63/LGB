# Spring Security JWT Template

## 1. Purpose

This document defines the standard JWT authentication structure for Spring Boot projects.

The goal is to keep authentication and authorization consistent, secure, and understandable.

## 2. Recommended Security Package Structure

```text
src/main/java/com.LGB/global/security/
├─ SecurityConfig.java
├─ JwtTokenProvider.java
└─ JwtAuthenticationFilter.java
```

## 3. Authentication Flow

Standard JWT login flow:

1. User submits email and password.
2. Backend validates credentials.
3. Backend creates JWT access token.
4. Frontend stores the token.
5. Frontend sends token in the Authorization header.
6. Backend validates the token for protected APIs.
7. Backend extracts user identity and role.
8. Backend allows or blocks the request.

## 4. Authorization Header Format

```text
Authorization: Bearer <token>
```

## 5. Spring Security Rules

- Passwords must be hashed.
- JWT secret must not be hardcoded.
- JWT secret must come from environment variables or secure config.
- Protected APIs must require authentication.
- Admin APIs must require admin role.
- Frontend-only hiding is not security.
- Backend must enforce role checks.
- Authentication failure should return 401.
- Authorization failure should return 403.
- Sensitive user data must not be included in JWT claims.

## 6. Required Dependencies

Typical JWT implementation may require a JWT library.

Example for Gradle:

```gradle
implementation 'io.jsonwebtoken:jjwt-api:0.12.6'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.6'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.6'
```

Example for Maven:

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

## 7. application.yml Example

```yaml
jwt:
  secret: ${JWT_SECRET}
  expiration-ms: ${JWT_EXPIRATION_MS:86400000}
```

## 8. API Security Checklist

- [ ] Public APIs are explicitly allowed.
- [ ] Protected APIs require authentication.
- [ ] Admin APIs require admin role.
- [ ] JWT secret is not committed.
- [ ] Token expiration is configured.
- [ ] Passwords are hashed.
- [ ] CORS is configured safely.
- [ ] Error responses do not expose internal details.
- [ ] Security rules are tested.

## 9. Codex Rule

When Codex works on Spring Security:

1. Check existing security configuration first.
2. Avoid overwriting working security logic without explanation.
3. Keep public and protected endpoints clearly separated.
4. Put role checks on the backend.
5. Explain how to test authentication and authorization.