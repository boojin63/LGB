# Audit Log Template

## 1. Purpose

This document defines the audit log policy for Spring Boot projects.

The goal is to record important user and admin actions for security, operation, and accountability.

## 2. Difference Between Logs and Audit Logs

| Type | Purpose |
|---|---|
| Application log | Debugging and operation |
| Audit log | Who did what, when, and from where |

Application logs help developers debug.

Audit logs help answer:

```text
Who deleted this data?
Who changed this setting?
Who approved this request?
When did this action happen?
```

## 3. Actions to Audit

Recommended audit targets:

- Login success/failure
- Logout
- Password change
- User role change
- User deletion
- Admin approval/rejection
- Important data create/update/delete
- Payment-related action
- Security-sensitive action
- Production configuration change

## 4. Audit Log Fields

Recommended fields:

| Field | Meaning |
|---|---|
| `id` | Audit log ID |
| `actorId` | User who performed action |
| `actorRole` | User role |
| `action` | Action name |
| `targetType` | Target resource type |
| `targetId` | Target resource ID |
| `requestId` | Request ID for log tracing |
| `ipAddress` | Client IP if available |
| `userAgent` | Client user agent if available |
| `createdAt` | Action time |

## 5. Example Actions

```text
USER_LOGIN
USER_LOGOUT
USER_PASSWORD_CHANGE
ADMIN_USER_ROLE_CHANGE
ADMIN_USER_DELETE
POST_CREATE
POST_UPDATE
POST_DELETE
RESERVATION_APPROVE
RESERVATION_REJECT
PAYMENT_REQUEST
PAYMENT_SUCCESS
PAYMENT_FAIL
```

## 6. Security Rules

- Do not store passwords.
- Do not store full JWT tokens.
- Do not store full payment secrets.
- Do not store unnecessary personal information.
- Limit audit log access to administrators.
- Treat audit logs as sensitive data.

## 7. Retention Rule

Recommended retention:

| Service Stage | Retention |
|---|---|
| MVP | 30-90 days |
| Production | 6-12 months |
| Regulated service | Follow legal/compliance requirements |

## 8. Query Use Cases

Audit logs should help answer:

```text
Which admin deleted this user?
When was this reservation approved?
Who changed this role?
What happened before this incident?
```

## 9. Checklist

- [ ] Important admin actions are audited.
- [ ] Important user security actions are audited.
- [ ] Audit logs do not contain secrets.
- [ ] Audit logs include actor and action.
- [ ] Audit logs include target when applicable.
- [ ] Audit logs include request ID when possible.
- [ ] Audit log access is restricted.
- [ ] Retention policy is defined.

## 10. Codex Rule

When Codex adds sensitive or admin features:

1. Check whether audit logging is needed.
2. Record actor, action, target, and request ID.
3. Do not log sensitive values.
4. Protect audit log read APIs.
5. Update audit documentation.