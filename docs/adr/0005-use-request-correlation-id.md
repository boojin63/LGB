# ADR 0005: Use Request ID / Correlation ID

## Status

Accepted

## Date

YYYY-MM-DD

## Context

The project needs a way to trace individual requests across logs and error reports.

Without a request identifier, production debugging becomes difficult because logs from many users and requests are mixed together.

## Options Considered

### Option 1: No request ID

Pros:

- No additional setup

Cons:

- Hard to trace production errors
- Support and debugging are slower
- Logs are harder to connect to user reports

### Option 2: Use Request ID with MDC

Pros:

- Easy to add to logs
- Helps trace each request
- Works well with Spring filters
- Useful for production debugging

Cons:

- Requires logging configuration
- Async tasks need extra MDC propagation handling

## Decision

Use Request ID with Spring filter and MDC.

## Reasons

1. It improves production debugging.
2. It helps connect frontend error reports to backend logs.
3. It is lightweight and easy to maintain.
4. It prepares the project for more advanced observability later.

## Consequences

### Positive

1. Logs become easier to search.
2. Error reports become easier to investigate.
3. Production incidents can be resolved faster.

### Negative

1. Logging pattern needs configuration.
2. Async execution needs careful MDC handling.

## Follow-up Actions

1. Add `RequestIdFilter`.
2. Add request ID to log pattern.
3. Return `X-Request-Id` header.
4. Avoid putting sensitive data in request IDs.