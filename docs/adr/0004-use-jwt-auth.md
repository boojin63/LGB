# ADR 0004: Use JWT Authentication

## Status

Accepted

## Date

YYYY-MM-DD

## Context

The project needs authentication for APIs and may need frontend-backend separation.

## Options Considered

### Option 1: JWT Authentication

Pros:

- Works well with stateless APIs
- Easy to use with frontend applications
- Common for REST API authentication
- Fits mobile/web clients

Cons:

- Token storage must be handled carefully
- Token invalidation can be harder
- Refresh token strategy may be needed later

### Option 2: Session-Based Authentication

Pros:

- Mature and common
- Server can invalidate sessions easily

Cons:

- Requires session storage
- Less convenient for some mobile/API clients

## Decision

Use JWT authentication as the default authentication strategy.

## Reasons

1. The template targets API-based Spring Boot projects.
2. JWT works well with separate frontend clients.
3. It is common in MVP web and mobile services.
4. It keeps backend APIs stateless.

## Consequences

### Positive

1. Frontend and backend can be separated easily.
2. APIs can be consumed by web and mobile clients.
3. Server-side session storage is not required.

### Negative

1. Token leakage is a security risk.
2. Token expiration and refresh strategy must be designed carefully.
3. Logout behavior requires frontend token removal or token blacklist strategy.

## Follow-up Actions

1. Store JWT secret in environment variables.
2. Use HTTPS in production.
3. Avoid storing sensitive data in JWT claims.
4. Consider refresh token strategy for real services.
5. Review token storage method on the frontend.