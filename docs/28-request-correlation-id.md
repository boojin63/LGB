# Request ID / Correlation ID Template

## 1. Purpose

This document defines how to use Request ID and Correlation ID in Spring Boot projects.

The goal is to trace one request across logs, errors, API calls, and debugging sessions.

## 2. Concept

### Request ID

A unique ID generated for each HTTP request.

Example:

```text
requestId=8f9d2a7c-1c2b-4d7a-9a3f-2b9f2c0a1234
```

### Correlation ID

An ID used to connect multiple related requests across systems.

For small projects, Request ID and Correlation ID can be the same value.

## 3. Why This Matters

Without Request ID:

```text
A user reports an error.
The logs contain many unrelated errors.
It is hard to find the exact request.
```

With Request ID:

```text
A user reports requestId=abc-123.
The developer searches logs by requestId.
The exact error flow can be found quickly.
```

## 4. Recommended Header

Use this HTTP header:

```text
X-Request-Id
```

If the client sends this header, reuse it.

If the client does not send it, the backend generates a new UUID.

## 5. Recommended Log Pattern

Recommended Spring logging pattern:

```yaml
logging:
  pattern:
    level: "%5p [requestId:%X{requestId}]"
```

Or:

```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [requestId:%X{requestId}] %logger{36} - %msg%n"
```

## 6. Recommended Files

```text
code-templates/spring/global/logging/LoggingConstants.java
code-templates/spring/global/logging/RequestIdFilter.java
code-templates/spring/global/config/MdcTaskDecorator.java
```

## 7. Spring MDC

MDC means Mapped Diagnostic Context.

It allows logs to automatically include request-specific data such as:

```text
requestId
userId
traceId
```

## 8. API Response Header

The backend should return the Request ID in the response header:

```text
X-Request-Id: generated-request-id
```

This helps frontend or support teams report exact errors.

## 9. Security Rule

Do not put sensitive data in Request ID.

Do not use:

- Email
- Phone number
- Token
- Password
- User private data

## 10. Checklist

- [ ] Request ID is generated if missing.
- [ ] Request ID is reused if provided.
- [ ] Request ID is added to MDC.
- [ ] Request ID is returned in response header.
- [ ] Logs include request ID.
- [ ] MDC is cleared after request.
- [ ] Sensitive data is not used as request ID.

## 11. Codex Rule

When Codex adds Request ID support:

1. Add a servlet filter.
2. Use MDC.
3. Return `X-Request-Id` response header.
4. Clear MDC in `finally`.
5. Update logging configuration.
6. Explain how to test it.