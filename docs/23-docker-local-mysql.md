# Docker + Local MySQL Template

## 1. Purpose

This document defines how to run a Spring Boot project with local MySQL using Docker.

The goal is to reduce environment differences and make local development easier.

## 2. What This Template Provides

This template provides:

```text
docker-templates/spring-mysql/Dockerfile
docker-templates/spring-mysql/docker-compose.local.yml
docker-templates/spring-mysql/.dockerignore
```

## 3. Recommended Local Development Structure

In an actual Spring project, copy these files to the project root:

```text
Dockerfile
docker-compose.local.yml
.dockerignore
```

## 4. Local MySQL Configuration

The local MySQL container uses:

| Item | Value |
|---|---|
| Database | `app_db` |
| Root password | `root_password` |
| App user | `app_user` |
| App password | `app_password` |
| Port | `3306` |

Local Spring datasource URL:

```text
jdbc:mysql://localhost:3306/app_db
```

When Spring runs inside Docker Compose, use:

```text
jdbc:mysql://mysql:3306/app_db
```

## 5. Commands

Start local MySQL and Spring app:

```bash
docker compose -f docker-compose.local.yml up --build
```

Start only MySQL:

```bash
docker compose -f docker-compose.local.yml up mysql
```

Stop containers:

```bash
docker compose -f docker-compose.local.yml down
```

Stop and remove database volume:

```bash
docker compose -f docker-compose.local.yml down -v
```

## 6. When to Use Docker

Use Docker when:

- Local MySQL setup is difficult.
- Team members need the same development environment.
- Deployment environment should be closer to local.
- You want repeatable setup.

## 7. Docker Safety Rules

- Do not use local Docker passwords in production.
- Do not commit real production secrets.
- Use environment variables in production.
- Do not mount sensitive files unnecessarily.
- Do not expose unnecessary ports.
- Use different DB credentials for local and production.

## 8. Troubleshooting

### Port 3306 already in use

Another MySQL server may already be running.

Options:

1. Stop local MySQL.
2. Change Docker port mapping.

Example:

```yaml
ports:
  - "3307:3306"
```

Then local connection URL becomes:

```text
jdbc:mysql://localhost:3307/app_db
```

### App starts before MySQL is ready

`depends_on` does not always mean the database is fully ready.

Solutions:

1. Restart the app container.
2. Add retry logic in Spring.
3. Use a healthcheck in Docker Compose.

## 9. Codex Rule

When Codex applies Docker to a Spring project:

1. Check whether the project uses Gradle or Maven.
2. Adjust Dockerfile build command accordingly.
3. Confirm the actual jar output path.
4. Keep local and production secrets separate.
5. Explain how to run and stop containers.