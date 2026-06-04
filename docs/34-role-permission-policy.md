# Role / Permission Policy Template

## 1. Purpose

This document defines role and permission policy for Spring Boot projects.

The goal is to prevent authorization confusion as the service grows.

## 2. Default Roles

Recommended basic roles:

| Role | Meaning |
|---|---|
| `GUEST` | Not logged-in user |
| `USER` | Normal logged-in user |
| `MANAGER` | Limited administrator |
| `ADMIN` | Full administrator |

## 3. Role Hierarchy

Recommended hierarchy:

```text
ADMIN
↓
MANAGER
↓
USER
↓
GUEST
```

Higher roles may include lower role permissions only when explicitly designed.

Do not assume hierarchy automatically unless implemented in Spring Security.

## 4. Permission Principles

- Authentication means the user is logged in.
- Authorization means the user has permission.
- Frontend hiding is not security.
- Backend must enforce every important permission.
- Admin APIs must verify admin role.
- Users must not access other users' private data.

## 5. Common Permission Rules

| Action | GUEST | USER | MANAGER | ADMIN |
|---|---:|---:|---:|---:|
| View public page | Yes | Yes | Yes | Yes |
| Register | Yes | No | No | No |
| Login | Yes | Yes | Yes | Yes |
| View own profile | No | Yes | Yes | Yes |
| Edit own profile | No | Yes | Yes | Yes |
| Delete own account | No | Yes | Yes | Yes |
| View admin dashboard | No | No | Yes | Yes |
| Manage users | No | No | No | Yes |
| Delete user data | No | No | No | Yes |

## 6. Ownership Rule

For user-owned resources:

```text
User can access only their own resources unless they are ADMIN or explicitly allowed.
```

Example:

```text
User A cannot edit User B's post.
User A cannot view User B's private reservation.
```

## 7. API Permission Example

| API | Required Role | Ownership Check |
|---|---|---|
| `GET /api/me` | USER | Own user only |
| `PATCH /api/me` | USER | Own user only |
| `GET /api/admin/users` | ADMIN | No |
| `DELETE /api/admin/users/{id}` | ADMIN | No |
| `POST /api/posts` | USER | Author becomes current user |
| `PATCH /api/posts/{id}` | USER | Author or ADMIN |
| `DELETE /api/posts/{id}` | USER | Author or ADMIN |

## 8. Spring Security Rule

Use Spring Security for route-level protection.

Example:

```java
.requestMatchers("/api/admin/**").hasRole("ADMIN")
.anyRequest().authenticated()
```

Use service-level checks for ownership.

Example:

```java
if (!post.isWrittenBy(currentUserId) && !currentUserRole.equals(RoleType.ADMIN)) {
    throw new CustomException(ErrorCode.FORBIDDEN);
}
```

## 9. JWT Role Rule

JWT may contain role information, but important authorization decisions should be verified carefully.

Do not store sensitive information in JWT.

Allowed claims:

```text
userId
email
role
```

Avoid:

```text
password
phone number
private address
sensitive personal data
```

## 10. Checklist

- [ ] Roles are defined.
- [ ] Admin APIs are protected.
- [ ] User-owned resources check ownership.
- [ ] Frontend hiding is not treated as security.
- [ ] JWT role usage is reviewed.
- [ ] Permission failure returns 403.
- [ ] Authentication failure returns 401.
- [ ] Permission rules are documented.

## 11. Codex Rule

When Codex adds or modifies a feature:

1. Identify required role.
2. Identify ownership rule.
3. Add backend permission checks.
4. Do not rely only on frontend UI.
5. Update API permission documentation.
6. Add permission-related tests if possible.