# Monitoring Template

## 1. Purpose

This document defines how the project should be monitored after deployment.

The goal is to detect problems quickly and understand production behavior.

## 2. What to Monitor

### Frontend

- [ ] Page load failure
- [ ] JavaScript errors
- [ ] Broken navigation
- [ ] API request failures
- [ ] Login/logout issues
- [ ] Important user action failures

### Backend

- [ ] Server uptime
- [ ] API error rate
- [ ] Slow API responses
- [ ] Authentication failures
- [ ] Database connection errors
- [ ] Unhandled exceptions

### Database

- [ ] Connection failures
- [ ] Slow queries
- [ ] Migration errors
- [ ] Storage limits
- [ ] Backup status

## 3. Basic Logs

Backend logs should include:

- Request method
- Request path
- Status code
- Error message
- Timestamp

Backend logs should not include:

- Passwords
- Tokens
- Full secret values
- Sensitive personal data

## 4. Recommended Basic Tools

Beginner-friendly options:

| Need | Tool Examples |
|---|---|
| Hosting logs | Render, Railway, Vercel logs |
| Frontend errors | Sentry, LogRocket |
| Backend errors | Sentry, hosting logs |
| Uptime check | Better Stack, UptimeRobot |
| Analytics | Plausible, Google Analytics |

## 5. Health Check

If backend exists, create a health check endpoint.

Example:

```text
GET /health
```

Expected response:

```json
{
  "success": true,
  "status": "ok"
}
```

## 6. Production Smoke Test

After deployment, check:

- [ ] Home page loads.
- [ ] Login works.
- [ ] Logout works.
- [ ] Main feature list loads.
- [ ] Main feature create works.
- [ ] Admin page works if applicable.
- [ ] Backend health check works.
- [ ] No critical error appears in logs.

## 7. Alert Rules

Set alerts for:

- [ ] Site down
- [ ] Backend down
- [ ] High error rate
- [ ] Database connection failure
- [ ] Repeated login failures
- [ ] Deployment failure

## 8. Monitoring Review Routine

Recommended routine:

| Frequency | Task |
|---|---|
| After every deployment | Check logs and smoke test |
| Daily | Check error logs |
| Weekly | Review performance and user issues |
| Monthly | Review database and backup status |

## 9. Incident Log

Use this format when an issue happens.

| Item | Value |
|---|---|
| Date |  |
| Issue |  |
| Severity |  |
| Cause |  |
| Fix |  |
| Prevention |  |