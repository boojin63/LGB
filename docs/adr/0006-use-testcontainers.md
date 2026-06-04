# ADR 0006: Use Testcontainers for Database Integration Tests

## Status

Accepted

## Date

YYYY-MM-DD

## Context

The project uses MySQL in production and needs reliable database integration tests.

Using only mocks or H2 can miss issues that happen specifically with MySQL.

## Options Considered

### Option 1: Mockito only

Pros:

- Fast
- Simple
- No Docker required

Cons:

- Cannot verify real database behavior
- Cannot verify SQL constraints or migration behavior

### Option 2: H2 database

Pros:

- Fast
- Easy setup

Cons:

- Different from MySQL
- Some SQL, type, and constraint behavior can differ

### Option 3: Testcontainers with MySQL

Pros:

- Uses real MySQL container
- Closer to production behavior
- Good for repository and migration tests

Cons:

- Requires Docker
- Slower than unit tests
- CI configuration may need Docker support

## Decision

Use Testcontainers for important database integration tests.

## Reasons

1. Production uses MySQL.
2. JPA and SQL behavior should be verified against MySQL.
3. Flyway migration can be tested more reliably.
4. It reduces deployment-time database surprises.

## Consequences

### Positive

1. Database tests become more realistic.
2. Migration issues are easier to catch.
3. Repository behavior is more trustworthy.

### Negative

1. Tests are slower.
2. Docker must be running.
3. CI environment must support containers.

## Follow-up Actions

1. Keep unit tests fast with Mockito.
2. Use Testcontainers only for integration tests.
3. Add documentation for running integration tests.
4. Check CI compatibility.