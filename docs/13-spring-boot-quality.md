# Spring Boot Quality Template

## 1. Purpose

This document defines quality standards for Spring Boot projects.

The goal is to make every Spring Boot backend project stable, secure, readable, and production-ready.

## 2. Spring Boot Project Quality

A Spring Boot project is acceptable only when:

- [ ] The project starts successfully.
- [ ] Environment profiles are separated.
- [ ] Database connection is configured safely.
- [ ] API responses are consistent.
- [ ] Exceptions are handled globally.
- [ ] Validation is applied to request DTOs.
- [ ] Security configuration is explicit.
- [ ] Secrets are not committed.
- [ ] Build command works.
- [ ] Local and production settings are separated.

## 3. Recommended Dependencies

Common dependencies:

- Spring Web
- Spring Data JPA
- Spring Security
- Validation
- MySQL Driver
- Lombok
- Spring Boot Test

Optional dependencies:

- springdoc-openapi
- Actuator
- Flyway or Liquibase
- Testcontainers

## 4. Profile Standard

Recommended profiles:

```text
local
dev
prod
```

Recommended files:

```text
application.yml
application-local.yml
application-prod.yml
```

## 5. application.yml Standard

Common settings should be in:

```text
application.yml
```

Local development settings should be in:

```text
application-local.yml
```

Production settings should be in:

```text
application-prod.yml
```

Environment-specific secrets should not be hardcoded.

Use environment variables for:

- Database URL
- Database username
- Database password
- JWT secret
- CORS origin

## 6. Build Quality

Before completing work:

- [ ] Gradle or Maven build passes.
- [ ] Tests pass if tests exist.
- [ ] Application starts locally.
- [ ] No critical warnings are ignored.
- [ ] No production secrets are committed.
- [ ] Local profile works.
- [ ] Production profile is safe.

## 7. API Quality

API is acceptable only when:

- [ ] Endpoint naming is predictable.
- [ ] Request DTO is validated.
- [ ] Response DTO is used.
- [ ] Error response is consistent.
- [ ] Authentication is checked where needed.
- [ ] Authorization is checked where needed.
- [ ] Sensitive fields are excluded.
- [ ] Entity is not returned directly.

## 8. Database Quality

Database layer is acceptable only when:

- [ ] Entity relationships are clear.
- [ ] Lazy loading is used appropriately.
- [ ] Migration strategy is considered.
- [ ] Production does not rely on unsafe schema auto-update.
- [ ] Indexes are considered for frequent lookups.
- [ ] Destructive changes are reviewed.
- [ ] Test data and production data are separated.

## 9. Security Quality

Security is acceptable only when:

- [ ] Passwords are hashed.
- [ ] JWT secret is stored in environment variables.
- [ ] Protected APIs require authentication.
- [ ] Admin APIs require admin role.
- [ ] Users cannot access other users' private data.
- [ ] CORS is configured safely.
- [ ] Error messages do not expose internal details.

## 10. Final Quality Gate

A Spring Boot backend is release-ready only when:

- [ ] Build passes.
- [ ] Application starts.
- [ ] Main APIs work.
- [ ] Auth works.
- [ ] Permission checks work.
- [ ] Database connection works.
- [ ] Production secrets are not committed.
- [ ] Deployment profile is checked.