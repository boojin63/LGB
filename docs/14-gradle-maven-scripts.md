# Gradle / Maven Scripts Template

## 1. Purpose

This document defines common build and run commands for Spring Boot projects.

The goal is to make every Spring project easy to run, test, build, and deploy.

## 2. Gradle Commands

Recommended Gradle commands:

```bash
./gradlew bootRun
./gradlew build
./gradlew test
./gradlew clean
```

For Windows PowerShell:

```powershell
.\gradlew bootRun
.\gradlew build
.\gradlew test
.\gradlew clean
```

## 3. Maven Commands

Recommended Maven commands:

```bash
./mvnw spring-boot:run
./mvnw clean package
./mvnw test
./mvnw clean
```

For Windows PowerShell:

```powershell
.\mvnw spring-boot:run
.\mvnw clean package
.\mvnw test
.\mvnw clean
```

## 4. Production Build

Gradle:

```bash
./gradlew clean build
```

Run jar:

```bash
java -jar build/libs/lgb.jar
```

Maven:

```bash
./mvnw clean package
```

Run jar:

```bash
java -jar target/lgb.jar
```

## 5. Recommended CI Commands

For Gradle:

```bash
./gradlew clean build
```

For Maven:

```bash
./mvnw clean verify
```

## 6. Build Tool Selection Rule

Use one build tool per project.

Recommended for beginners:

```text
Gradle
```

Maven is also acceptable if the project already uses Maven or the developer prefers Maven.

## 7. Codex Rule

When Codex works on a Spring project, it should:

1. Detect whether the project uses Gradle or Maven.
2. Use the correct wrapper command.
3. Prefer `gradlew` or `mvnw` over globally installed tools.
4. Explain build failures clearly.
5. Avoid changing build files without reason.
6. Keep dependency changes minimal.
7. Explain why a new dependency is needed.