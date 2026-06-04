# Test Plan Template

## 1. Test Scope

This document defines manual and automated test cases for the project.

Project name:

```text

```

Testing goal:

```text

```

## 2. Test Environment

| Item | Value |
|---|---|
| Frontend URL |  |
| Backend URL |  |
| Database |  |
| Browser |  |
| Device |  |

## 3. Authentication Tests

- [ ] User can sign up.
- [ ] User can log in.
- [ ] User can log out.
- [ ] Login session is restored after refresh.
- [ ] Invalid email shows error.
- [ ] Invalid password shows error.
- [ ] Protected pages block unauthenticated users.
- [ ] Expired token is handled correctly.

Notes:

```text

```

## 4. Role Permission Tests

| Test | Expected Result | Pass |
|---|---|---|
| Guest accesses protected page | Blocked | [ ] |
| Normal user accesses admin page | Forbidden | [ ] |
| Admin accesses admin page | Allowed | [ ] |
| User edits own data | Allowed | [ ] |
| User edits another user's data | Blocked | [ ] |
| User calls admin API directly | Blocked | [ ] |

Notes:

```text

```

## 5. Main Feature Tests

Feature name:

```text

```

Checklist:

- [ ] List loads correctly.
- [ ] Detail page loads correctly.
- [ ] Create works.
- [ ] Update works.
- [ ] Delete works.
- [ ] Search works.
- [ ] Filter works.
- [ ] Sort works.
- [ ] Empty state appears.
- [ ] Loading state appears.
- [ ] Error state appears.
- [ ] Success message appears.

Notes:

```text

```

## 6. Form Validation Tests

| Field | Invalid Input | Expected Result | Pass |
|---|---|---|---|
| email | invalid email | Error message | [ ] |
| password | too short | Error message | [ ] |
| title | empty | Error message | [ ] |
|  |  |  | [ ] |

## 7. API Failure Tests

- [ ] Server down
- [ ] Invalid request body
- [ ] Missing token
- [ ] Expired token
- [ ] Wrong role
- [ ] Resource not found
- [ ] Duplicate request
- [ ] Database error

Notes:

```text

```

## 8. UI State Tests

Each major screen must handle:

- [ ] Loading state
- [ ] Empty state
- [ ] Error state
- [ ] Success state
- [ ] Permission denied state
- [ ] Not found state

Screen-specific notes:

```text

```

## 9. Regression Tests

After each change, check:

- [ ] Login still works.
- [ ] Logout still works.
- [ ] Main navigation still works.
- [ ] Existing list pages still work.
- [ ] Existing detail pages still work.
- [ ] Existing create/update/delete flows still work.
- [ ] Admin pages still work.
- [ ] User pages still work.
- [ ] No new console errors.
- [ ] No new server errors.

## 10. Deployment Tests

- [ ] Frontend builds successfully.
- [ ] Backend builds successfully.
- [ ] Environment variables are configured.
- [ ] Database connection works.
- [ ] Prisma migration works.
- [ ] Login works in production.
- [ ] API URL is correct in production.
- [ ] CORS is configured.
- [ ] No secrets are exposed.

## 11. Release Checklist

- [ ] Critical features work.
- [ ] Critical bugs are fixed.
- [ ] Security review is complete.
- [ ] README is updated.
- [ ] API documentation is updated.
- [ ] Deployment guide is updated.
- [ ] Manual test completed.
- [ ] Release decision approved.

## 12. Final Test Result

Status:

```text
PASS / FAIL / PARTIAL
```

Notes:

```text

```