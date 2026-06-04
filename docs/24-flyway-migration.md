# Flyway Migration Template

## 1. Purpose

This document defines the database migration standard for Spring Boot projects using Flyway.

The goal is to manage database schema changes safely and predictably.

Flyway stores schema changes as versioned migration files, which helps prevent manual DB changes and inconsistent schema states.

## 2. Why Use Flyway

Without Flyway:

```text
Developer A changes DB manually.
Developer B does not know the change.
Production DB becomes different from local DB.
```

With Flyway:

```text
Every DB change is written as a versioned SQL file.
The same migration runs on local, staging, and production.
Schema history is tracked.
```

## 3. Recommended Folder

In actual Spring projects:

```text
src/main/resources/db/migration/
```

Example:

```text
src/main/resources/db/migration/V1__init.sql
src/main/resources/db/migration/V2__add_posts_table.sql
src/main/resources/db/migration/V3__add_user_status.sql
```

## 4. Naming Rule

Flyway versioned migration naming format:

```text
V{version}__{description}.sql
```

Examples:

```text
V1__init.sql
V2__create_users_table.sql
V3__add_posts_table.sql
V4__add_index_to_posts.sql
```

Rules:

- Use `V` prefix.
- Use version number.
- Use double underscore `__`.
- Use clear English description.
- Never rename already-applied migration files.
- Never edit already-applied migration files in shared environments.

## 5. Spring Boot Dependency

Gradle:

```gradle
implementation 'org.flywaydb:flyway-core'
implementation 'org.flywaydb:flyway-mysql'
```

Maven:

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```

## 6. application.yml Example

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

Production JPA setting:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

## 7. Important Rule

Do not use this in production:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

For production, prefer:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

And let Flyway manage schema changes.

## 8. Migration Workflow

When DB schema needs to change:

1. Create a new migration file.
2. Write SQL changes.
3. Run application locally.
4. Confirm migration succeeds.
5. Confirm schema changed correctly.
6. Commit migration file.
7. Deploy.
8. Check migration result in logs or Actuator Flyway endpoint if enabled.

## 9. Rollback Principle

Flyway Community version does not automatically rollback versioned migrations.

For risky changes:

1. Backup production DB.
2. Avoid destructive changes.
3. Use additive changes first.
4. Deploy application compatibility changes.
5. Remove old columns later.

## 10. Destructive Change Warning

Be careful with:

- Dropping columns
- Renaming columns
- Changing column types
- Dropping tables
- Adding NOT NULL columns without default
- Deleting data

## 11. Checklist

- [ ] Migration file uses correct naming.
- [ ] Migration file is committed.
- [ ] Already-applied migration was not edited.
- [ ] Local migration succeeds.
- [ ] Production `ddl-auto` is not `update`.
- [ ] Destructive changes are reviewed.
- [ ] Backup is considered for production.
- [ ] Application code matches schema.

## 12. Codex Rule

When Codex changes database schema:

1. Do not rely only on JPA auto-update.
2. Create a Flyway migration plan.
3. Explain migration risk.
4. Avoid destructive changes unless explicitly approved.
5. Update DB documentation.