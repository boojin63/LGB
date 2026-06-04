# ADR 0007: Use Docker Production Build

## Status

Accepted

## Date

YYYY-MM-DD

## Context

The project needs a repeatable way to package and run Spring Boot applications in production-like environments.

Manual server setup can cause environment differences and deployment mistakes.

## Options Considered

### Option 1: Run jar directly on server

Pros:

- Simple
- No Docker required

Cons:

- Server environment must be configured manually
- Java version mismatch can happen
- Harder to standardize deployments

### Option 2: Docker production image

Pros:

- Repeatable runtime environment
- Easier deployment with Docker Compose
- Works well with CI/CD
- Easier rollback by image tag

Cons:

- Requires Docker knowledge
- Image build and registry setup are needed

## Decision

Use Docker production build as the recommended deployment packaging method.

## Reasons

1. It standardizes runtime environment.
2. It reduces “works on my machine” issues.
3. It supports CI/CD workflows.
4. It makes deployment and rollback more predictable.

## Consequences

### Positive

1. Deployment process becomes more repeatable.
2. Production server setup becomes simpler.
3. Rollback can be image-based.

### Negative

1. Docker must be installed and managed.
2. Image registry credentials must be protected.
3. Dockerfile must match the actual build tool.

## Follow-up Actions

1. Maintain production Dockerfile template.
2. Use environment variables for secrets.
3. Do not commit production `.env`.
4. Document deployment and rollback steps.