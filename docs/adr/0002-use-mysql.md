# ADR 0002: Use MySQL

## Status

Accepted

## Date

YYYY-MM-DD

## Context

The project needs a relational database for storing users, business data, relationships, and transactional records.

## Options Considered

### Option 1: MySQL

Pros:

- Widely used
- Easy to host
- Works well with Spring Data JPA
- Good for common web services
- Beginner-friendly resources are common

Cons:

- Requires schema design
- Some advanced scaling cases need careful planning

### Option 2: PostgreSQL

Pros:

- Strong relational database
- Good advanced features
- Excellent for complex data

Cons:

- Slightly more advanced for beginners
- Some hosting defaults may be less familiar

### Option 3: NoSQL

Pros:

- Flexible schema
- Useful for some document-style data

Cons:

- Not ideal for strongly relational business data
- Can make relationships and transactions harder

## Decision

Use MySQL as the default database.

## Reasons

1. It is suitable for most MVP business platforms.
2. It works well with JPA.
3. It is easy to run locally with Docker.
4. It is easy to deploy using many beginner-friendly platforms.

## Consequences

### Positive

1. Schema and relationships are clear.
2. Works well with Spring Data JPA.
3. Easy to find examples and support.

### Negative

1. Schema changes must be managed carefully.
2. Migration tools such as Flyway become important.

## Follow-up Actions

1. Use Flyway for production schema changes.
2. Avoid unsafe `ddl-auto: update` in production.
3. Document important schema decisions.