# Spring Boot Actuator Template

## 1. Purpose

This document defines how to use Spring Boot Actuator for health checks and production monitoring.

The goal is to make deployed Spring applications easier to observe and diagnose.

Spring Boot Actuator exposes operational endpoints under `/actuator`. For example, the health endpoint is commonly available at `/actuator/health`.

## 2. Why Use Actuator

Actuator helps check:

- Application health
- Application info
- Metrics
- DB health
- Flyway migration information
- Runtime status

## 3. Recommended Dependency

Gradle:

```gradle
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

Maven:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

## 4. Recommended Configuration

Use this template:

```text
config-templates/backend/actuator/actuator-application-template.yml
```

Recommended exposed endpoints:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,flyway
```

## 5. Common Endpoints

| Endpoint | Purpose |
|---|---|
| `/actuator/health` | Application health |
| `/actuator/info` | Application information |
| `/actuator/metrics` | Application metrics |
| `/actuator/flyway` | Flyway migration information |

The Flyway endpoint provides information about database migrations performed by Flyway when available.

## 6. Security Warning

Do not expose all Actuator endpoints publicly in production.

Avoid:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

For production, expose only what is needed.

Recommended public endpoint:

```text
/actuator/health
```

Sensitive endpoints should require authentication or be blocked from public access.

## 7. Health Check Usage

Local health check:

```bash
curl http://localhost:8080/actuator/health
```

Expected basic result:

```json
{
  "status": "UP"
}
```

## 8. Deployment Usage

Hosting services can use `/actuator/health` as a health check path.

Examples:

```text
Health Check Path: /actuator/health
```

## 9. Checklist

- [ ] Actuator dependency added.
- [ ] `/actuator/health` works.
- [ ] Only safe endpoints are exposed.
- [ ] Sensitive endpoints are protected.
- [ ] Health check path is configured in hosting.
- [ ] Production exposure is reviewed.
- [ ] Flyway endpoint is enabled only when needed.

## 10. Codex Rule

When Codex adds Actuator:

1. Add dependency only if missing.
2. Expose minimum necessary endpoints.
3. Do not expose all endpoints in production without approval.
4. Configure health check.
5. Explain security implications.