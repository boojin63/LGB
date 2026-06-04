# Security Checklist Template

## 1. Purpose

This document defines a stronger security checklist for new projects.

The goal is to prevent common security mistakes before release.

## 2. Authentication

- [ ] Login is required for protected pages.
- [ ] Protected APIs reject missing tokens.
- [ ] Invalid tokens are rejected.
- [ ] Expired tokens are handled safely.
- [ ] Logout clears local/session token.
- [ ] Token storage method is documented.
- [ ] Password reset flow is safe if implemented.

Notes:

```text

```

## 3. Authorization

- [ ] Admin-only APIs check admin role.
- [ ] Users cannot access other users' private data.
- [ ] Users cannot edit or delete other users' data.
- [ ] Role checks are enforced on the backend, not only frontend.
- [ ] Hidden frontend buttons are not treated as security protection.
- [ ] Permission errors return 403.

Notes:

```text

```

## 4. Password Security

- [ ] Passwords are hashed.
- [ ] Plain text passwords are never stored.
- [ ] Plain text passwords are never returned.
- [ ] Password validation exists.
- [ ] Login failure message does not reveal too much.

Notes:

```text

```

## 5. Input Validation

- [ ] Request body is validated.
- [ ] Required fields are checked.
- [ ] Invalid data types are rejected.
- [ ] Long text input is limited.
- [ ] Uploaded files are validated if file upload exists.
- [ ] Query parameters are sanitized.

Notes:

```text

```

## 6. Sensitive Data

- [ ] Password fields are excluded from API responses.
- [ ] Tokens are not logged.
- [ ] API keys are not exposed to frontend.
- [ ] Internal errors are not exposed.
- [ ] Personal data exposure is minimized.
- [ ] Admin-only data is protected.

Notes:

```text

```

## 7. Environment Variables

- [ ] `.env` is included in `.gitignore`.
- [ ] `.env.example` exists if needed.
- [ ] Production secrets are not committed.
- [ ] JWT secret is strong.
- [ ] Database URL is not exposed.
- [ ] Frontend only uses public-safe env variables.

Notes:

```text

```

## 8. API Security

- [ ] API uses consistent auth middleware.
- [ ] Rate limiting is considered for login and public APIs.
- [ ] CORS is configured correctly.
- [ ] Dangerous operations require confirmation.
- [ ] Delete actions are protected.
- [ ] Admin APIs are not guessable-only protected.

Notes:

```text

```

## 9. Database Security

- [ ] ORM or parameterized queries are used.
- [ ] Raw SQL is avoided unless necessary.
- [ ] Raw SQL is reviewed carefully if used.
- [ ] Database user permissions are limited if possible.
- [ ] Destructive migration is reviewed.
- [ ] Backups are considered before production migration.

Notes:

```text

```

## 10. Frontend Security

- [ ] Sensitive data is not stored unnecessarily.
- [ ] Admin UI is hidden from normal users.
- [ ] Frontend does not assume hidden UI equals security.
- [ ] External links use safe attributes when needed.
- [ ] User-generated content is escaped or sanitized.

Notes:

```text

```

## 11. Security Severity

| Level | Meaning | Release Impact |
|---|---|---|
| Critical | Data leak, auth bypass, secret exposure | Block release |
| High | Admin/user permission broken | Usually block release |
| Medium | Validation weakness or limited exposure | Fix soon |
| Low | Minor hardening issue | Backlog acceptable |

## 12. Final Security Decision

Status:

```text
PASS / NEEDS FIX / BLOCK RELEASE
```

Critical issues:

1.
2.
3.

Required fixes:

1.
2.
3.