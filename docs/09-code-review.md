# Code Review Template

## 1. Purpose

This document defines the code review standard for this project.

The goal is to prevent bugs, security issues, messy code, and broken features before merging changes.

## 2. Review Summary

| Item | Description |
|---|---|
| Branch |  |
| Feature/Fix |  |
| Reviewer |  |
| Date |  |
| Status | PASS / FAIL / NEEDS CHANGES |

## 3. Review Scope

What is being reviewed?

```text

```

Files changed:

```text

```

## 4. Product Review

- [ ] The change matches the original request.
- [ ] The feature solves a real user need.
- [ ] The scope is not larger than necessary.
- [ ] No unnecessary feature was added.
- [ ] The MVP direction is preserved.

Notes:

```text

```

## 5. UX/UI Review

- [ ] Main user flow is clear.
- [ ] Buttons and actions are easy to understand.
- [ ] Loading state exists.
- [ ] Empty state exists.
- [ ] Error state exists.
- [ ] Success state exists.
- [ ] Mobile layout is considered.
- [ ] Destructive actions require confirmation.

Notes:

```text

```

## 6. Frontend Review

- [ ] Components are readable.
- [ ] State management is understandable.
- [ ] API calls use the existing API client.
- [ ] Mock data is not used in production flow.
- [ ] Loading, empty, error, and success states are handled.
- [ ] No unnecessary library was added.
- [ ] Console errors are removed.
- [ ] Reusable code is extracted when useful.

Notes:

```text

```

## 7. Backend Review

- [ ] API route is predictable.
- [ ] Request body is validated.
- [ ] Authentication is checked where needed.
- [ ] Authorization is checked where needed.
- [ ] Error responses are consistent.
- [ ] Business logic is not duplicated unnecessarily.
- [ ] Sensitive data is not returned.
- [ ] Database access is done through the expected ORM/query layer.

Notes:

```text

```

## 8. Database Review

- [ ] Schema change is necessary.
- [ ] Migration impact is understood.
- [ ] Required fields have safe defaults when needed.
- [ ] Relationships are correct.
- [ ] Indexes are added only when useful.
- [ ] No destructive change is made without approval.
- [ ] Seed data is updated if needed.

Notes:

```text

```

## 9. Security Review

- [ ] Secrets are not committed.
- [ ] Environment variables are used correctly.
- [ ] User input is validated.
- [ ] Admin-only actions are protected.
- [ ] Users cannot access other users' private data.
- [ ] Error messages do not expose internal details.
- [ ] Token handling is safe.
- [ ] Passwords are never returned to the frontend.

Notes:

```text

```

## 10. QA Review

- [ ] Main success flow was tested.
- [ ] Error flow was tested.
- [ ] Empty state was tested.
- [ ] Permission failure was tested.
- [ ] Refresh behavior was tested.
- [ ] Existing features were checked.
- [ ] No new critical bug was found.

Notes:

```text

```

## 11. Documentation Review

- [ ] README updated if needed.
- [ ] API docs updated if needed.
- [ ] DB docs updated if needed.
- [ ] Deployment notes updated if needed.
- [ ] Environment variables documented if needed.

Notes:

```text

```

## 12. Final Decision

Select one:

```text
PASS
NEEDS CHANGES
FAIL
```

Reason:

```text

```

Required fixes before merge:

1.
2.
3.

Nice-to-have improvements:

1.
2.
3.