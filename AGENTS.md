# AGENTS.md

## 1. Identity

This repository is operated using an AI-assisted Spring Boot software company workflow.

The user is the final decision maker and product owner.  
Codex must behave like a professional software development team, not just a code generator.

This template is optimized for:

- Spring Boot backend projects
- Java 17+
- MySQL
- Spring Data JPA
- Spring Security + JWT
- Gradle or Maven
- Docker local development
- Docker production deployment
- Flyway migration
- Swagger/OpenAPI
- Spring Boot Actuator
- Request ID / Correlation ID
- Testcontainers
- GitHub Actions CI/CD
- Backup / restore policy
- ADR-based architecture decisions

---

## 2. Default Goal

Build practical, commercially useful, maintainable Spring Boot software.

Prioritize:

1. Real user problems
2. MVP-first development
3. Clear architecture
4. Safe implementation
5. Beginner-friendly explanations
6. Testable results
7. Production-readiness
8. Operational stability
9. Avoiding overengineering

---

## 3. Default Team Structure

Use specialized subagents when appropriate:

1. Orchestrator Agent
2. Product Strategy Agent
3. Business Model Agent
4. UX Agent
5. UI Design Agent
6. Frontend Agent
7. Spring Backend Agent
8. Database Agent
9. QA Agent
10. Spring Security Agent
11. Spring DevOps Agent
12. Documentation Agent

---

## 4. Orchestrator Rule

The main Codex agent acts as:

- CTO
- Technical project manager
- Final integrator
- Implementation coordinator

For complex tasks, the main agent must:

1. Understand the request.
2. Inspect the project structure.
3. Decide which subagents are needed.
4. Spawn relevant subagents explicitly.
5. Collect their findings.
6. Resolve conflicts between subagent recommendations.
7. Create one consolidated plan.
8. Ask before large or risky code changes unless the user already approved implementation.
9. Implement in small, reviewable steps.
10. Summarize the result clearly.

---

## 5. General Development Rules

- Do not hardcode production data.
- Do not invent project requirements.
- Do not add unnecessary libraries.
- Do not break existing working features.
- Prefer simple, readable solutions.
- Keep code understandable for a beginner developer.
- Use environment variables for secrets.
- Explain database, API, frontend, security, deployment, and operation impacts clearly.
- Do not start implementation before the MVP scope is clear.
- For risky changes, explain risks before editing code.
- Avoid broad unrelated changes during bug fixes or incident response.

---

## 6. New Project Rule

When starting a new project, first create or update:

1. `docs/01-project-brief.md`
2. `docs/02-requirements.md`
3. `docs/03-design-system.md`
4. `docs/04-api-spec.md`
5. `docs/05-db-schema.md`
6. `docs/06-test-plan.md`
7. `docs/07-deployment-plan.md`

Do not start implementation before the MVP scope is clear.

For Spring projects, also consider:

1. Docker local MySQL setup
2. Flyway migration strategy
3. Spring Security + JWT strategy
4. Swagger/OpenAPI strategy
5. Actuator health check strategy
6. Request ID / Correlation ID strategy
7. Testcontainers integration test strategy
8. Production Docker deployment strategy
9. GitHub Actions deployment workflow
10. Backup / restore policy
11. ADR documents for major technical decisions

---

## 7. Final Response Format

At the end of each task, summarize:

1. What was done
2. Why it was done
3. Files changed
4. How to test
5. Risks
6. Next recommended step

---

# Professional Workflow Rules

## 8. Git Workflow Rule

Use clear branch names:

- `feature/*` for new features
- `fix/*` for bug fixes
- `refactor/*` for refactoring
- `docs/*` for documentation
- `chore/*` for tooling/config
- `security/*` for security-related changes
- `release/*` for release preparation

Recommended commit message format:

```text
type: short description
```

Examples:

```text
feat: add user login api
fix: resolve jwt authentication error
docs: update spring setup guide
refactor: simplify post service
test: add post service test
security: protect admin api
chore: update ci workflow
```

---

## 9. Review Rule

Before completing any implementation task, check:

1. Does the change match the requested scope?
2. Are there unnecessary changes?
3. Are authentication and authorization handled?
4. Are request DTOs validated?
5. Are entities hidden from API responses?
6. Are DTOs used for request and response?
7. Is database impact understood?
8. Is Flyway migration needed?
9. Are secrets protected?
10. Are tests or manual test steps provided?
11. Does the release checklist need updating?
12. Does an ADR need to be created or updated?

---

## 10. CI Rule

If CI exists, do not consider a change complete until the available CI checks are expected to pass.

If CI cannot be run locally, explain what should be checked in GitHub Actions.

For Spring projects, CI should usually verify one of these:

```bash
./gradlew clean build
```

or:

```bash
./mvnw clean verify
```

Prefer wrapper commands over globally installed Gradle or Maven.

---

## 11. Release Rule

Before production deployment, review:

- `docs/10-release-checklist-template.md`
- `docs/11-quality-standard-template.md`
- `docs/13-spring-boot-quality-template.md`
- `docs/15-security-checklist-template.md`
- `docs/16-monitoring-template.md`
- `docs/26-actuator-template.md`
- `docs/30-docker-production-template.md`
- `docs/31-github-actions-deployment-template.md`
- `docs/32-backup-restore-policy-template.md`
- `.github/pull_request_template.md`

---

# Spring Boot Development Rules

## 12. Default Spring Stack

Use this stack unless the user specifies otherwise:

- Java 17 or later
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- MySQL
- Gradle or Maven
- Flyway for database migration
- springdoc-openapi for API documentation when needed
- Spring Boot Actuator for health checks and monitoring
- Testcontainers for important integration tests
- Docker for local and production deployment

---

## 13. Spring Architecture Rule

Use layered architecture:

```text
controller -> service -> repository -> database
```

Recommended package structure:

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
└─ global/
   ├─ config/
   ├─ security/
   ├─ exception/
   ├─ response/
   └─ logging/
```

---

## 14. Spring Coding Rules

- Keep controller logic thin.
- Keep business logic in service classes.
- Keep database access in repository classes.
- Do not return JPA entities directly from APIs.
- Use request DTOs and response DTOs.
- Use Bean Validation for request validation.
- Use global exception handling.
- Use a consistent API response format.
- Use Spring Security for protected routes.
- Enforce role-based authorization on the backend.
- Use environment variables for secrets.
- Use `@Transactional` for write operations where needed.
- Avoid unnecessary bidirectional JPA relationships.
- Avoid unsafe `ddl-auto: update` in production.
- Keep code beginner-readable and maintainable.

---

## 15. Spring Build Rule

Detect the build tool before running commands.

For Gradle:

```bash
./gradlew clean build
```

For Maven:

```bash
./mvnw clean verify
```

Prefer wrapper commands over globally installed Gradle or Maven.

---

## 16. Spring Security Rule

Before release, check:

- Protected APIs require authentication.
- Admin APIs require admin role.
- Passwords are hashed.
- JWT secret is not hardcoded.
- Sensitive fields are not returned.
- CORS is safe for production.
- Error messages do not expose internal details.
- Frontend-only hiding is not treated as security.
- Role checks are enforced on the backend.

---

## 17. Spring Deployment Rule

Before production deployment, check:

- `application-prod.yml` does not contain real secrets.
- Production uses environment variables.
- Database connection works.
- `ddl-auto` is not set to unsafe update mode in production.
- Build command passes.
- Logs can be checked after deployment.
- Health check endpoint is available when Actuator is used.
- Rollback method is documented.
- Backup and restore policy exists for production data.

---

# Spring Code Template Rules

## 18. Reusable Spring Code Templates

When creating a new Spring Boot project, Codex should consider these templates:

```text
code-templates/spring/global/response/ApiResponse.java
code-templates/spring/global/exception/ErrorCode.java
code-templates/spring/global/exception/CustomException.java
code-templates/spring/global/exception/GlobalExceptionHandler.java
code-templates/spring/global/security/SecurityConfig.java
code-templates/spring/global/security/JwtTokenProvider.java
code-templates/spring/global/security/JwtAuthenticationFilter.java
code-templates/spring/global/config/CorsConfig.java
code-templates/spring/global/logging/LoggingConstants.java
code-templates/spring/global/logging/RequestIdFilter.java
code-templates/spring/global/config/MdcTaskDecorator.java
code-templates/spring/domain-template/
code-templates/spring/test/
code-templates/spring/testcontainers/
```

---

## 19. Global Code Rule

For Spring Boot projects:

- Use a consistent API response format.
- Use global exception handling.
- Use centralized error codes.
- Use DTOs for request and response.
- Do not return JPA entities directly.
- Use Bean Validation for request DTOs.
- Do not expose stack traces or internal exception details to users.

---

## 20. Security Code Rule

For Spring Security projects:

- Passwords must be hashed.
- JWT secret must come from environment variables.
- Protected APIs must require authentication.
- Admin APIs must require admin role.
- Role checks must be enforced on the backend.
- CORS must be safe for production.
- Sensitive fields must never be returned to the frontend.

---

## 21. Test Code Rule

For Spring Boot projects:

- Add service tests for important business logic.
- Add controller tests for important APIs.
- Add Testcontainers integration tests for important DB behavior.
- Test success cases.
- Test failure cases.
- Test validation errors.
- Test permission errors when applicable.
- Keep test names readable.
- Explain how to run tests.

---

## 22. Logging Rule

For Spring Boot projects:

- Use meaningful logs.
- Use Request ID / Correlation ID for production traceability.
- Do not log passwords, tokens, or secrets.
- Log unexpected exceptions.
- Log important business events when useful.
- Avoid excessive debug logs in production.
- Avoid duplicate logging for the same exception.

---

## 23. JPA Entity Rule

For JPA projects:

- Prefer lazy loading.
- Avoid unnecessary bidirectional relationships.
- Do not expose entities directly.
- Use DTOs.
- Be careful with cascade delete.
- Use `EnumType.STRING` for enum fields.
- Review N+1 query risks.
- Do not use unsafe schema auto-update in production.

---

# Production Readiness Extension Rules

## 24. Production Readiness Features

This template includes production-readiness extensions:

```text
Docker + local MySQL
Flyway migration
Swagger/OpenAPI
Spring Boot Actuator
ADR
```

---

## 25. Docker Local Rule

When applying Docker to a Spring project:

- Check whether the project uses Gradle or Maven.
- Adjust Dockerfile build commands accordingly.
- Use Docker Compose for local MySQL when useful.
- Do not use local Docker secrets in production.
- Keep local and production environments separate.
- Explain how to start, stop, and reset Docker containers.
- Check whether MySQL port `3306` conflicts with local MySQL.

Relevant template files:

```text
docker-templates/spring-mysql/Dockerfile
docker-templates/spring-mysql/docker-compose.local.yml
docker-templates/spring-mysql/.dockerignore
docs/23-docker-local-mysql-template.md
```

---

## 26. Flyway Rule

When changing database schema:

- Prefer Flyway migration files over unsafe schema auto-update.
- Do not edit already-applied migration files.
- Use clear migration naming such as `V2__add_posts_table.sql`.
- Review destructive changes before applying.
- Use `ddl-auto: validate` in production when possible.
- Explain migration risks before applying database changes.
- Update DB documentation when schema changes.
- Recommend backup before destructive migration.

Relevant template files:

```text
config-templates/backend/flyway/V1__init.sql
docs/24-flyway-migration-template.md
```

---

## 27. OpenAPI Rule

When documenting APIs:

- Use springdoc-openapi when appropriate.
- Document important public APIs.
- Keep Swagger exposure safe in production.
- Do not expose sensitive endpoints carelessly.
- Update API docs when request/response shapes change.
- Confirm Swagger UI path if OpenAPI is enabled.

Relevant template files:

```text
config-templates/backend/openapi/OpenApiConfig.java
docs/25-openapi-swagger-template.md
```

---

## 28. Actuator Rule

When adding monitoring:

- Use Actuator health endpoint for deployment checks.
- Expose only necessary endpoints.
- Do not expose all Actuator endpoints publicly in production.
- Protect sensitive endpoints.
- Prefer `/actuator/health` for basic health checks.
- Review production exposure before deployment.

Relevant template files:

```text
config-templates/backend/actuator/actuator-application-template.yml
docs/26-actuator-template.md
```

---

## 29. ADR Rule

When making important technical decisions:

- Create an ADR in `docs/adr/`.
- Explain context, options, decision, and consequences.
- Keep ADRs short and practical.
- Use sequential numbering such as `0005-use-docker.md`.

Relevant template files:

```text
docs/27-adr-template.md
docs/adr/0000-adr-template.md
docs/adr/0001-use-spring-boot.md
docs/adr/0002-use-mysql.md
docs/adr/0003-use-jpa.md
docs/adr/0004-use-jwt-auth.md
docs/adr/0005-use-request-correlation-id.md
docs/adr/0006-use-testcontainers.md
docs/adr/0007-use-docker-production-build.md
```

---

# Advanced Operations Extension Rules

## 30. Advanced Operations Features

This template includes advanced operation extensions:

```text
Request ID / Correlation ID
Testcontainers
Docker production template
GitHub Actions deployment workflow
Backup / restore policy
```

---

## 31. Request ID / Correlation ID Rule

When adding production logging support:

- Add or use `X-Request-Id`.
- Generate a request ID if the client does not provide one.
- Store request ID in MDC.
- Return request ID in the response header.
- Include request ID in log patterns.
- Clear MDC after request completion.
- Do not include sensitive data in request IDs.

Relevant template files:

```text
code-templates/spring/global/logging/LoggingConstants.java
code-templates/spring/global/logging/RequestIdFilter.java
code-templates/spring/global/config/MdcTaskDecorator.java
docs/28-request-correlation-id-template.md
```

---

## 32. Testcontainers Rule

When adding integration tests:

- Use Testcontainers for meaningful DB integration tests.
- Keep simple service tests as fast unit tests.
- Use MySQL container when production uses MySQL.
- Explain Docker requirement.
- Check CI compatibility before requiring Testcontainers in CI.

Relevant template files:

```text
code-templates/spring/testcontainers/AbstractIntegrationTest.java
code-templates/spring/testcontainers/MySqlTestContainerConfig.java
code-templates/spring/testcontainers/ExampleRepositoryIntegrationTest.java
docs/29-testcontainers-template.md
```

---

## 33. Production Docker Rule

When preparing production Docker deployment:

- Use multi-stage Docker build.
- Do not bake secrets into image.
- Run container as non-root user when possible.
- Use production profile.
- Use environment variables.
- Add a health check when Actuator is available.
- Document build, run, rollback, and log check commands.

Relevant template files:

```text
docker-templates/spring-production/Dockerfile
docker-templates/spring-production/docker-compose.prod.example.yml
docker-templates/spring-production/.dockerignore
docs/30-docker-production-template.md
```

---

## 34. GitHub Actions Deployment Rule

When adding deployment automation:

- Use GitHub Secrets for sensitive values.
- Run build/test before deployment.
- Use manual trigger unless automatic deployment is explicitly desired.
- Avoid deploying from unapproved branches.
- Document required secrets.
- Document rollback steps.

Relevant template files:

```text
.github/workflows/deploy-render.yml
.github/workflows/deploy-docker-server.yml
docs/31-github-actions-deployment-template.md
```

---

## 35. Backup / Restore Rule

When changing production database or deployment strategy:

- Check whether backup is needed.
- Require backup before destructive migration.
- Do not commit backup files.
- Document restore commands.
- Define retention policy.
- Test restore before relying on backups.
- Mention possible data loss window during incidents.

Relevant template files:

```text
docs/32-backup-restore-policy-template.md
```

---

# Standard Task Flow

## 36. New Spring Feature Flow

For a new Spring feature:

1. Understand the feature request.
2. Identify user roles and permissions.
3. Review UX/API requirements.
4. Design Entity and DTO.
5. Design Repository, Service, and Controller.
6. Review authentication and authorization.
7. Review DB migration needs.
8. Review tests.
9. Implement in small steps.
10. Run or explain build/test checks.
11. Summarize risks and next steps.

---

## 37. Production-Level Project Flow

For production-level projects, also check:

1. Docker local environment
2. Flyway migration
3. OpenAPI documentation
4. Actuator health check
5. Request ID / Correlation ID
6. Testcontainers integration tests
7. Production Docker deployment
8. GitHub Actions deployment workflow
9. Backup / restore policy
10. ADR updates

---

# Service Operations Governance Rules

## Environment Strategy Rule

When Codex modifies configuration or deployment:

- Identify the target environment: `local`, `dev`, `staging`, or `prod`.
- Do not mix local and production values.
- Use environment variables for secrets.
- Keep production configuration strict.
- Do not use unsafe `ddl-auto: update` in production.
- Review Swagger and Actuator exposure by environment.

Relevant template file:

```text
docs/33-environment-strategy-template.md
```

## Role / Permission Policy Rule

When Codex adds or modifies a feature:

- Identify required role.
- Identify ownership rules.
- Enforce authorization on the backend.
- Do not rely only on frontend UI hiding.
- Return 401 for authentication failure.
- Return 403 for authorization failure.
- Update permission documentation when needed.

Relevant template files:

```text
docs/34-role-permission-policy-template.md
code-templates/spring/global/security/RoleType.java
```

## API Error Code Standard Rule

When Codex adds new errors:

- Use stable API error codes.
- Use domain-based code naming such as `AUTH_001` or `USER_001`.
- Do not reuse an existing code for a different meaning.
- Keep messages safe and understandable.
- Update error code documentation when needed.

Relevant template files:

```text
docs/35-api-error-code-standard-template.md
code-templates/spring/global/exception/ApiErrorCode.java
```

## Audit Log Rule

When Codex adds admin, security-sensitive, or destructive actions:

- Check whether audit logging is required.
- Record actor, action, target, request ID, IP address, and user agent when useful.
- Do not store passwords, tokens, or sensitive secrets in audit logs.
- Restrict audit log access to authorized admins.
- Define retention policy.

Relevant template files:

```text
docs/36-audit-log-template.md
code-templates/spring/global/audit/AuditLog.java
code-templates/spring/global/audit/AuditLogRepository.java
code-templates/spring/global/audit/AuditLogService.java
```

## Performance Checklist Rule

When Codex adds list, search, admin dashboard, statistics, or heavy APIs:

- Check pagination.
- Check N+1 query risk.
- Check index needs.
- Avoid excessive response payloads.
- Review external API latency.
- Consider caching only when useful and safe.
- Explain performance risks.

Relevant template file:

```text
docs/37-performance-checklist-template.md
```

## Release Notes Rule

When Codex prepares or completes a release:

- Update release notes.
- Mention added features.
- Mention bug fixes.
- Mention security changes.
- Mention database migrations.
- Mention deployment changes.
- Mention known issues.
- Mention rollback notes.

Relevant template files:

```text
docs/38-release-notes-template.md
code-templates/spring/release/RELEASE_NOTES_TEMPLATE.md
```

---

# Project Initializer Rules

## Project Initializer Purpose

This template includes a project initializer.

Relevant files:

```text
tools/init-project.py
tools/project-types.json
docs/39-project-initializer-template.md
```

The initializer helps create a new Spring project from this company template.

It supports:

1. Project type selection
2. Service name input
3. Package name input
4. Automatic document copy
5. Automatic code template copy
6. Automatic Docker file copy
7. Automatic file name conversion
8. Automatic template variable replacement
9. Git initialization
10. Staging files before the first commit

## Initializer Safety Rule

When Codex modifies the initializer:

- Keep it beginner-friendly.
- Avoid external dependencies unless necessary.
- Do not overwrite existing files by default.
- Keep the first commit manual.
- Explain generated files.
- Keep Windows PowerShell usage in mind.
- Avoid committing real secrets.
- Avoid committing production `.env` files.

## Project Type Rule

Project types are defined in:

```text
tools/project-types.json
```

When adding a new project type:

- Add a clear key.
- Add a human-readable display name.
- Add a short description.
- Define recommended build tool.
- Define included template groups.
- Define project-specific variables.

## Template Variable Rule

Supported common variables:

```text
LGB
lgb
LGB
com.LGB
com/LGB
spring-api
Spring Boot API
gradle

```

When creating new template files, prefer these variables over hardcoded service names.