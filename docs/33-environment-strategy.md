# Environment Strategy Template

## 1. Purpose

This document defines the environment strategy for Spring Boot projects.

The goal is to prevent configuration mistakes between local, development, staging, and production environments.

## 2. Environment Types

Recommended environments:

| Environment | Purpose |
|---|---|
| `local` | Developer's local machine |
| `dev` | Shared development server |
| `staging` | Production-like test environment |
| `prod` | Real user production environment |

## 3. Environment Rule

Each environment must have separate configuration.

Recommended files:

```text
application.yml
application-local.yml
application-dev.yml
application-staging.yml
application-prod.yml
```

## 4. Configuration Separation

### Common Config

Use `application.yml` for common values.

Examples:

```yaml
spring:
  application:
    name: app-name

server:
  port: ${SERVER_PORT:8080}
```

### Local Config

Use `application-local.yml` for local development.

Allowed:

- Local DB URL
- Local CORS origin
- Local test secret
- Debug-friendly logging

### Dev Config

Use `application-dev.yml` for shared development server.

Allowed:

- Development DB URL
- Development CORS origin
- Development logging level

### Staging Config

Use `application-staging.yml` for pre-production verification.

This should be similar to production.

Allowed:

- Staging DB URL
- Staging CORS origin
- Production-like security settings

### Production Config

Use `application-prod.yml` for real users.

Production config must be strict.

Required:

- Real secrets from environment variables
- Safe CORS origin
- `ddl-auto: validate`
- Minimal public actuator endpoints
- Production logging level

## 5. Required Environment Variables

Recommended variables:

```text
SPRING_PROFILES_ACTIVE
SERVER_PORT

DB_URL
DB_USERNAME
DB_PASSWORD

JWT_SECRET
JWT_EXPIRATION_MS

CORS_ALLOWED_ORIGINS

APP_VERSION
```

Optional:

```text
SENTRY_DSN
LOG_LEVEL
SWAGGER_ENABLED
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE
```

## 6. Production Safety Rules

- Do not commit production `.env`.
- Do not hardcode production secrets.
- Do not use local DB credentials in production.
- Do not expose all actuator endpoints.
- Do not expose Swagger UI publicly without review.
- Do not use `ddl-auto: update` in production.
- Use Flyway for production schema changes.

## 7. Deployment Checklist

Before deploying to production:

- [ ] `SPRING_PROFILES_ACTIVE=prod`
- [ ] `DB_URL` is set.
- [ ] `DB_USERNAME` is set.
- [ ] `DB_PASSWORD` is set.
- [ ] `JWT_SECRET` is secure.
- [ ] `CORS_ALLOWED_ORIGINS` is production domain.
- [ ] Flyway migration reviewed.
- [ ] Actuator exposure reviewed.
- [ ] Swagger exposure reviewed.
- [ ] Backup exists before risky migration.

## 8. Environment Comparison Table

| Item | local | dev | staging | prod |
|---|---|---|---|---|
| DB | Local MySQL | Dev DB | Staging DB | Production DB |
| CORS | localhost | dev domain | staging domain | production domain |
| Swagger | Allowed | Allowed | Optional | Restricted |
| Actuator | Broad | Limited | Limited | Minimal |
| ddl-auto | update allowed | validate preferred | validate | validate |
| Secrets | Local only | Dev secrets | Staging secrets | Production secrets |

## 9. Codex Rule

When Codex modifies environment configuration:

1. Identify the target environment.
2. Do not mix local and production values.
3. Use environment variables for secrets.
4. Explain which environment file is affected.
5. Check deployment impact.
6. Check security impact.