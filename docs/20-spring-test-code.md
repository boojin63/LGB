# Spring Test Code Template

## 1. Purpose

This document defines the basic testing standard for Spring Boot projects.

The goal is to verify core backend behavior before release.

## 2. Recommended Test Types

| Test Type | Purpose |
|---|---|
| Unit Test | Test service logic in isolation |
| Controller Test | Test API request/response behavior |
| Repository Test | Test database query behavior |
| Integration Test | Test multiple layers together |

## 3. Beginner-Friendly Priority

For beginner projects, start with:

1. Service tests
2. Controller tests
3. Repository tests if needed
4. Integration tests later

## 4. Service Test Checklist

- [ ] Main success case is tested.
- [ ] Invalid input case is tested.
- [ ] Not found case is tested.
- [ ] Permission-related case is tested if applicable.
- [ ] Repository dependency is mocked if unit test.

## 5. Controller Test Checklist

- [ ] API path is correct.
- [ ] Request body is accepted.
- [ ] Response status is correct.
- [ ] Response body format is correct.
- [ ] Validation failure is tested.
- [ ] Auth failure is tested if applicable.

## 6. Test Naming Rule

Use readable test names.

Example:

```java
@Test
void createExample_success() {
}
```

Or Korean display names can be used:

```java
@DisplayName("예시 데이터를 생성할 수 있다")
@Test
void createExample_success() {
}
```

## 7. Codex Rule

When Codex writes Spring tests:

1. Start with important business logic.
2. Avoid testing implementation details too much.
3. Keep tests readable.
4. Use clear test names.
5. Explain how to run tests.

## 8. Commands

Gradle:

```bash
./gradlew test
```

Windows PowerShell:

```powershell
.\gradlew test
```

Maven:

```bash
./mvnw test
```

Windows PowerShell:

```powershell
.\mvnw test
```