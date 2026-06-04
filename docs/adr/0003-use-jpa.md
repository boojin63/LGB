# ADR 0003: Use Spring Data JPA

## Status

Accepted

## Date

YYYY-MM-DD

## Context

The project needs a way to access relational database data from Spring Boot applications.

## Options Considered

### Option 1: Spring Data JPA

Pros:

- Reduces repetitive SQL code
- Integrates well with Spring Boot
- Supports entity relationships
- Common in business applications

Cons:

- Can cause N+1 query problems if used carelessly
- Requires understanding of entity lifecycle
- Relationship mapping can become complex

### Option 2: MyBatis

Pros:

- SQL control is explicit
- Easier to optimize certain queries

Cons:

- More SQL must be written manually
- More boilerplate for simple CRUD

### Option 3: Raw JDBC

Pros:

- Maximum control

Cons:

- Too much boilerplate
- Higher risk of inconsistent data access code

## Decision

Use Spring Data JPA as the default data access approach.

## Reasons

1. It is productive for CRUD-heavy MVP applications.
2. It works well with Spring Boot.
3. It supports clear domain modeling.
4. It reduces repetitive repository code.

## Consequences

### Positive

1. Faster development for common CRUD features.
2. Less boilerplate code.
3. Good integration with transactions.

### Negative

1. Developers must understand lazy loading.
2. N+1 query risks must be reviewed.
3. Entity relationships must be designed carefully.

## Follow-up Actions

1. Prefer lazy loading.
2. Avoid unnecessary bidirectional relationships.
3. Use DTOs for API responses.
4. Review query performance for list APIs.