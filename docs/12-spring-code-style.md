# Spring Code Style Template

## 1. Purpose

This document defines the common code style for Spring Boot projects.

The goal is to keep backend code readable, maintainable, secure, and easy to review.

## 2. Recommended Backend Stack

- Java 17 or later
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- MySQL
- Gradle or Maven

## 3. Recommended Package Structure

```text
src/main/java/com.LGB/
├─ domain/
│  └─ {feature}/
│     ├─ controller/
│     ├─ service/
│     ├─ repository/
│     ├─ entity/
│     └─ dto/
│
├─ global/
│  ├─ config/
│  ├─ security/
│  ├─ exception/
│  └─ response/
```

## 4. Layer Rules

### Controller

Controller is responsible for:

- Mapping HTTP routes
- Receiving request DTOs
- Returning response DTOs
- Calling service methods

Controller should not contain complex business logic.

### Service

Service is responsible for:

- Business logic
- Transaction handling
- Permission-related service checks
- Calling repositories

### Repository

Repository is responsible for:

- Database access
- Query methods
- Custom queries if needed

### Entity

Entity is responsible for:

- Database table mapping
- Relationship mapping
- Basic domain behavior if appropriate

Entity should not be returned directly to the frontend.

### DTO

DTO is responsible for:

- Request data
- Response data
- Validation annotations

## 5. Naming Rules

| Type | Example |
|---|---|
| Controller | `UserController` |
| Service | `UserService` |
| Repository | `UserRepository` |
| Entity | `User` |
| Request DTO | `UserCreateRequest` |
| Response DTO | `UserResponse` |
| Exception | `UserNotFoundException` |

## 6. Code Quality Rules

- Do not return entities directly from APIs.
- Use DTOs for request and response.
- Use Bean Validation for request validation.
- Use `@Transactional` for write operations when needed.
- Keep methods small and readable.
- Avoid duplicated business logic.
- Avoid unnecessary inheritance.
- Avoid hardcoded production values.
- Keep secrets in environment variables.
- Use global exception handling.
- Keep API response format consistent.

## 7. JPA Rules

- Prefer lazy loading for relationships.
- Avoid unnecessary bidirectional relationships.
- Avoid N+1 query problems.
- Use indexes for frequently searched fields.
- Be careful with cascade delete.
- Do not use `ddl-auto: update` in production.
- Use DTOs instead of exposing entities directly.
- Review migration impact before schema changes.

## 8. Error Handling Rules

Use global exception handling.

Recommended location:

```text
global/exception/GlobalExceptionHandler.java
```

Common error response:

```json
{
  "success": false,
  "message": "Error message"
}
```

## 9. Checklist

- [ ] Controller is thin.
- [ ] Service contains business logic.
- [ ] Repository only handles data access.
- [ ] DTOs are used.
- [ ] Entities are not exposed directly.
- [ ] Validation exists.
- [ ] Exceptions are handled globally.
- [ ] Security rules are checked.
- [ ] Environment variables are used.