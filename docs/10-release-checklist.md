# Release Checklist Template

## 1. Release Information

| Item | Value |
|---|---|
| Release version |  |
| Release date |  |
| Release owner |  |
| Target environment | local / staging / production |
| Related branch |  |
| Related PR |  |

## 2. Release Summary

What is included in this release?

```text

```

## 3. Feature Checklist

- [ ] New features are implemented.
- [ ] Changed features are tested.
- [ ] Removed features are documented.
- [ ] User-facing changes are confirmed.
- [ ] Admin-facing changes are confirmed.

Notes:

```text

```

## 4. Code Quality Checklist

- [ ] Code builds successfully.
- [ ] Type checks pass.
- [ ] Lint checks pass.
- [ ] Formatting checks pass.
- [ ] No unused debug code remains.
- [ ] No unnecessary console logs remain.
- [ ] No temporary test code remains.

Notes:

```text

```

## 5. Frontend Checklist

- [ ] Main pages load correctly.
- [ ] Navigation works.
- [ ] Forms work.
- [ ] Loading states work.
- [ ] Empty states work.
- [ ] Error states work.
- [ ] Responsive layout is checked.
- [ ] Production API URL is correct.

Notes:

```text

```

## 6. Backend Checklist

- [ ] Server starts successfully.
- [ ] Health check works if available.
- [ ] API routes work.
- [ ] Request validation works.
- [ ] Error handling works.
- [ ] Authentication works.
- [ ] Authorization works.
- [ ] CORS is configured correctly.

Notes:

```text

```

## 7. Database Checklist

- [ ] Database connection works.
- [ ] Prisma/client generation works if applicable.
- [ ] Migration is reviewed.
- [ ] Migration is applied safely.
- [ ] Seed data is checked if needed.
- [ ] No destructive schema change is unapproved.
- [ ] Backup is considered before production migration.

Notes:

```text

```

## 8. Security Checklist

- [ ] `.env` is not committed.
- [ ] Secrets are not exposed.
- [ ] JWT secret is configured.
- [ ] Admin APIs are protected.
- [ ] User data access is restricted.
- [ ] Passwords are hashed.
- [ ] Sensitive fields are not returned.
- [ ] Error messages do not leak internal details.

Notes:

```text

```

## 9. QA Checklist

- [ ] Main user flow tested.
- [ ] Admin flow tested if applicable.
- [ ] Guest flow tested if applicable.
- [ ] Invalid input tested.
- [ ] API failure tested.
- [ ] Permission failure tested.
- [ ] Regression test completed.

Notes:

```text

```

## 10. Deployment Checklist

- [ ] Hosting environment is ready.
- [ ] Environment variables are configured.
- [ ] Build command is configured.
- [ ] Start command is configured.
- [ ] Database URL is configured.
- [ ] Production domain is configured if needed.
- [ ] Deployment logs are checked.
- [ ] Production smoke test is completed.

Notes:

```text

```

## 11. Rollback Checklist

Rollback method:

```text

```

Rollback is possible?

```text
YES / NO
```

Rollback steps:

1.
2.
3.

## 12. Final Release Decision

Status:

```text
READY
NOT READY
RELEASED
FAILED
```

Reason:

```text

```

Approver:

```text

```