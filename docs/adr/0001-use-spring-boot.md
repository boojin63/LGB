# ADR 0001: Use Spring Boot

## Status

Accepted

## Date

YYYY-MM-DD

## Context

The project needs a reliable backend framework for building REST APIs, authentication, database integration, validation, and deployment-ready services.

## Options Considered

### Option 1: Spring Boot

Pros:

- Strong ecosystem
- Good for REST APIs
- Good database integration with JPA
- Good security support through Spring Security
- Widely used in business applications

Cons:

- More initial structure than lightweight frameworks
- Requires understanding of Spring concepts

### Option 2: Express

Pros:

- Lightweight
- Fast to start
- Simple for small APIs

Cons:

- More architecture decisions must be made manually
- Security and validation structure can become inconsistent

## Decision

Use Spring Boot as the default backend framework.

## Reasons

1. It provides a stable structure for backend projects.
2. It works well with MySQL, JPA, validation, and security.
3. It is suitable for business-oriented web services.
4. It helps enforce a more professional backend architecture.

## Consequences

### Positive

1. Backend structure becomes more standardized.
2. Security and validation can be handled systematically.
3. Long-term maintainability improves.

### Negative

1. Initial learning curve is higher.
2. Small projects may feel heavier than necessary.

## Follow-up Actions

1. Maintain Spring code style documentation.
2. Use layered architecture.
3. Keep beginner-friendly explanations in project documents.