# Testcontainers Template

## 1. Purpose

This document defines how to use Testcontainers in Spring Boot projects.

The goal is to run integration tests with a real database container instead of relying only on mocks or an in-memory database.

## 2. Why Use Testcontainers

Without Testcontainers:

```text
Tests use H2.
Production uses MySQL.
Some SQL or JPA behavior is different.
Bugs appear only after deployment.
```

With Testcontainers:

```text
Tests run against a real MySQL container.
Database behavior is closer to production.
Repository and integration tests are more reliable.
```

## 3. Recommended Use Cases

Use Testcontainers for:

- Repository tests
- Integration tests
- Flyway migration tests
- Query behavior tests
- JPA relationship tests
- DB constraint tests

Do not use Testcontainers for every tiny unit test.

## 4. Recommended Dependencies

Gradle:

```gradle
testImplementation 'org.testcontainers:junit-jupiter'
testImplementation 'org.testcontainers:mysql'
testImplementation 'org.springframework.boot:spring-boot-testcontainers'
```

Maven:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-testcontainers</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mysql</artifactId>
    <scope>test</scope>
</dependency>
```

## 5. Recommended Files

```text
code-templates/spring/testcontainers/AbstractIntegrationTest.java
code-templates/spring/testcontainers/MySqlTestContainerConfig.java
code-templates/spring/testcontainers/ExampleRepositoryIntegrationTest.java
```

## 6. Requirements

Testcontainers requires Docker to be running.

Before running integration tests:

```bash
docker --version
docker ps
```

## 7. Test Naming Rule

Recommended naming:

```text
*IntegrationTest.java
```

Examples:

```text
UserRepositoryIntegrationTest.java
PostRepositoryIntegrationTest.java
AuthIntegrationTest.java
```

## 8. Test Strategy

Use this split:

| Test Type | Tool |
|---|---|
| Service unit test | Mockito |
| Controller test | MockMvc |
| Repository integration test | Testcontainers |
| Full API integration test | SpringBootTest + Testcontainers |

## 9. Checklist

- [ ] Docker is running.
- [ ] Testcontainers dependencies are added.
- [ ] MySQL container starts.
- [ ] Spring datasource points to the container.
- [ ] Flyway migration works in test if enabled.
- [ ] Repository integration test passes.
- [ ] CI environment can run Docker if needed.

## 10. Codex Rule

When Codex adds Testcontainers:

1. Add dependencies only if missing.
2. Create integration tests only for meaningful DB behavior.
3. Do not replace fast unit tests unnecessarily.
4. Explain Docker requirement.
5. Explain how to run the tests.