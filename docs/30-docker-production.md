# Docker Production Template

## 1. Purpose

This document defines a production-oriented Docker build template for Spring Boot projects.

The goal is to build and run Spring Boot applications consistently across environments.

## 2. What This Template Provides

```text
docker-templates/spring-production/Dockerfile
docker-templates/spring-production/docker-compose.prod.example.yml
docker-templates/spring-production/.dockerignore
```

## 3. Production Dockerfile Strategy

Recommended strategy:

```text
Build stage:
- Use JDK image
- Copy Gradle wrapper and source code
- Build bootJar

Runtime stage:
- Use JRE image
- Copy only final jar
- Run application
```

This keeps the final image smaller and cleaner than shipping the full build environment.

## 4. Required Environment Variables

Production containers should use environment variables.

Recommended:

```text
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

DB_URL=jdbc:mysql://production-db-host:3306/app_db
DB_USERNAME=prod_user
DB_PASSWORD=prod_password

JWT_SECRET=replace-with-secure-secret
JWT_EXPIRATION_MS=86400000

CORS_ALLOWED_ORIGINS=https://example.com
```

## 5. Production Safety Rules

- Do not bake secrets into Docker images.
- Do not commit production `.env` files.
- Use platform secrets or server environment variables.
- Use `SPRING_PROFILES_ACTIVE=prod`.
- Do not use local MySQL credentials in production.
- Do not expose database ports publicly unless required.
- Use HTTPS in production.
- Check logs after deployment.

## 6. Build Command

```bash
docker build -t lgb:latest .
```

## 7. Run Command

```bash
docker run --rm \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/app_db \
  -e DB_USERNAME=prod_user \
  -e DB_PASSWORD=prod_password \
  -e JWT_SECRET=replace-with-secure-secret \
  -e CORS_ALLOWED_ORIGINS=https://example.com \
  lgb:latest
```

## 8. Docker Compose Production Example

Use:

```text
docker-templates/spring-production/docker-compose.prod.example.yml
```

Copy to actual project as:

```text
docker-compose.prod.yml
```

Do not commit real production secrets.

## 9. Checklist

- [ ] Production Dockerfile exists.
- [ ] Docker image builds.
- [ ] Runtime image does not contain unnecessary source/build files.
- [ ] Secrets are injected through environment variables.
- [ ] Production profile is active.
- [ ] Health check is available.
- [ ] Logs are visible.
- [ ] Rollback method is known.

## 10. Codex Rule

When Codex adds production Docker support:

1. Check Gradle or Maven build tool.
2. Use multi-stage build.
3. Avoid hardcoded production secrets.
4. Add `.dockerignore`.
5. Explain build and run commands.
6. Explain deployment risks.