# Project Initializer Template

## 1. Purpose

This document defines how to use the project initializer.

The goal is to create a new Spring project from this company template with less manual copying and fewer mistakes.

## 2. What the Initializer Does

The initializer supports:

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

## 3. Files

```text
tools/init-project.py
tools/project-types.json
```

## 4. Supported Project Types

Project types are defined in:

```text
tools/project-types.json
```

Initial project types:

| Type | Meaning |
|---|---|
| `spring-api` | Basic Spring Boot REST API |
| `spring-discord-bot` | Spring Boot Discord Bot SaaS |
| `spring-admin-api` | Spring Boot Admin API |

## 5. How to Run

From the template repository root:

```bash
python tools/init-project.py
```

Windows PowerShell:

```powershell
python tools\init-project.py
```

If `python` does not work:

```powershell
py tools\init-project.py
```

## 6. Inputs

The initializer asks:

```text
Project type
Service name
Package name
Target project folder
Build tool
Overwrite existing files?
Prepare git first commit state?
```

Example:

```text
Project type: spring-discord-bot
Service name: ModPilot
Package name: com.modpilot
Target folder: C:\dev\modpilot
Build tool: gradle
```

## 7. Output

The initializer creates or copies:

```text
AGENTS.md
docs/
.codex/
.github/
src/main/resources/application.yml
src/main/resources/application-local.yml
src/main/resources/application-prod.yml
src/main/resources/db/migration/V1__init.sql
src/main/java/{package}/global/
Dockerfile
docker-compose.local.yml
docker-compose.prod.example.yml
.dockerignore
build.gradle or pom.xml
.project-init.json
```

## 8. File Name Conversion

The initializer converts template names.

Examples:

```text
01-project-brief-template.md
↓
01-project-brief.md

application-template.yml
↓
application.yml

build.gradle.template
↓
build.gradle
```

## 9. Variable Replacement

The initializer replaces common template values.

Examples:

```text
com.LGB
↓
com.modpilot

lgb
↓
modpilot

LGB
↓
ModPilot
```

It also supports explicit template variables:

```text
LGB
lgb
com.LGB
spring-api
Spring Boot API
gradle
```

## 10. Git Behavior

If selected, the initializer runs:

```bash
git init
git add .
git status
```

It does not create the first commit automatically.

The developer should review files first and then run:

```bash
git commit -m "chore: initialize project"
```

## 11. Safety Rules

- Do not blindly overwrite existing files.
- Review generated files before first commit.
- Check package names in Java files.
- Check `application-prod.yml`.
- Check Docker environment variables.
- Check Flyway migration before running production DB.
- Do not commit real `.env` files.
- Do not commit backup files.

## 12. Codex Rule

When Codex uses or modifies the project initializer:

1. Keep it dependency-light.
2. Prefer standard library tools.
3. Do not require complex setup.
4. Keep Windows usage in mind.
5. Avoid destructive overwrite by default.
6. Explain generated files clearly.
7. Keep the first commit manual.