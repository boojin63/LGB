# API Error Code Standard Template

## 1. Purpose

This document defines standard API error codes for Spring Boot projects.

The goal is to make error handling predictable for frontend, backend, QA, and operations.

## 2. Error Response Format

Recommended error response:

```json
{
  "success": false,
  "data": null,
  "message": "User not found.",
  "code": "USER_001"
}
```

If the project uses a simpler response format, keep it consistent.

## 3. Error Code Naming Rule

Recommended format:

```text
DOMAIN_NUMBER
```

Examples:

```text
COMMON_001
AUTH_001
USER_001
POST_001
RESERVATION_001
PAYMENT_001
```

## 4. Common Error Domains

| Domain | Meaning |
|---|---|
| `COMMON` | General errors |
| `AUTH` | Authentication errors |
| `USER` | User-related errors |
| `ADMIN` | Admin-related errors |
| `POST` | Post/content errors |
| `FILE` | File upload errors |
| `DB` | Database errors |
| `EXTERNAL` | External API errors |

## 5. Recommended Common Codes

| Code | HTTP Status | Message |
|---|---:|---|
| `COMMON_001` | 400 | Invalid request. |
| `COMMON_002` | 400 | Validation failed. |
| `COMMON_003` | 404 | Resource not found. |
| `COMMON_004` | 409 | Resource already exists. |
| `COMMON_999` | 500 | Internal server error. |

## 6. Recommended Auth Codes

| Code | HTTP Status | Message |
|---|---:|---|
| `AUTH_001` | 401 | Authentication is required. |
| `AUTH_002` | 401 | Invalid token. |
| `AUTH_003` | 401 | Token has expired. |
| `AUTH_004` | 403 | Access denied. |
| `AUTH_005` | 400 | Invalid login credentials. |

## 7. Recommended User Codes

| Code | HTTP Status | Message |
|---|---:|---|
| `USER_001` | 404 | User not found. |
| `USER_002` | 409 | Email already exists. |
| `USER_003` | 400 | Invalid user status. |
| `USER_004` | 403 | Cannot access another user's data. |

## 8. Error Code Rules

- Error codes must be stable.
- Do not reuse an old code for a different meaning.
- Do not expose internal stack traces.
- Messages should be understandable.
- Frontend can use code for specific UI handling.
- Logs can include code for debugging.
- Security-sensitive errors should not reveal too much.

## 9. Frontend Usage

Frontend may use error code to show specific messages.

Example:

```text
AUTH_003 → "로그인이 만료되었습니다. 다시 로그인해주세요."
USER_002 → "이미 가입된 이메일입니다."
```

## 10. Checklist

- [ ] Error code has domain prefix.
- [ ] HTTP status matches error meaning.
- [ ] Message is understandable.
- [ ] Error does not expose sensitive details.
- [ ] Frontend handling is possible.
- [ ] Error code is documented.
- [ ] Error code is not duplicated.

## 11. Codex Rule

When Codex adds new business errors:

1. Add an error code.
2. Use consistent HTTP status.
3. Keep message user-safe.
4. Update error documentation.
5. Add test cases for important errors if possible.