# Spring Logging Template

## 1. Purpose

This document defines the logging standard for Spring Boot projects.

The goal is to make production issues easier to detect and debug without exposing sensitive information.

## 2. What to Log

Recommended logs:

- Server start and stop
- Important API failures
- Authentication failures
- Authorization failures
- Database connection issues
- External API failures
- Unexpected exceptions
- Important business events

## 3. What Not to Log

Never log:

- Plain text passwords
- JWT access tokens
- Refresh tokens
- Full secret values
- Full database URLs with credentials
- Payment secrets
- Sensitive personal information

## 4. Logging Levels

| Level | Purpose | Example |
|---|---|---|
| ERROR | Serious failure | Unexpected server error |
| WARN | Suspicious or recoverable issue | Invalid login attempt |
| INFO | Important normal event | Server started |
| DEBUG | Development details | SQL/debug info |
| TRACE | Very detailed debug | Rarely needed |

## 5. Recommended Usage

Use SLF4J.

Example:

```java
private static final Logger log = LoggerFactory.getLogger(ExampleService.class);
```

Example usage:

```java
log.info("Example created. id={}", exampleId);
log.warn("Example not found. id={}", id);
log.error("Unexpected error occurred", exception);
```

## 6. Controller Logging Rule

Avoid logging every normal request manually.

Use framework or middleware-style logging if needed.

## 7. Service Logging Rule

Log important business events only.

Examples:

- User registered
- Admin approved request
- Payment completed
- Critical operation failed

## 8. Exception Logging Rule

Unexpected exceptions should be logged once in the global exception handler or infrastructure layer.

Avoid duplicate logs for the same exception.

## 9. Production Log Checklist

- [ ] Logs do not expose secrets.
- [ ] Important failures are logged.
- [ ] Unexpected errors are logged.
- [ ] Logs include enough context.
- [ ] Logs are readable in hosting environment.
- [ ] Debug logs are not too noisy in production.

## 10. Codex Rule

When Codex adds logging:

1. Do not log sensitive data.
2. Avoid excessive logs.
3. Use parameterized logs.
4. Add logs only where they help debugging or operations.
5. Explain why the log is needed.