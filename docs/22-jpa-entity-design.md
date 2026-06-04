# JPA Entity Design Template

## 1. Purpose

This document defines JPA entity design standards for Spring Boot projects.

The goal is to prevent common JPA problems and keep database design maintainable.

## 2. Entity Design Principles

- Keep entities simple.
- Use DTOs for API requests and responses.
- Do not expose entities directly to the frontend.
- Avoid unnecessary bidirectional relationships.
- Prefer lazy loading.
- Be careful with cascade options.
- Review migration impact before changing fields.

## 3. Basic Entity Structure

Recommended fields:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private LocalDateTime createdAt;

private LocalDateTime updatedAt;
```

## 4. Relationship Rules

### Many-to-One

Recommended:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private User user;
```

### One-to-Many

Use only when needed.

```java
@OneToMany(mappedBy = "user")
private List<Post> posts = new ArrayList<>();
```

Do not add bidirectional relationships automatically.

## 5. Fetch Strategy

| Relationship | Recommended Fetch |
|---|---|
| ManyToOne | LAZY |
| OneToMany | LAZY |
| OneToOne | LAZY if possible |
| ManyToMany | Avoid if possible |

## 6. Cascade Rule

Use cascade carefully.

Avoid using `CascadeType.REMOVE` unless you fully understand the delete behavior.

## 7. Enum Rule

Use enums for clear status values.

Example:

```java
public enum ReservationStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED
}
```

Store enum safely:

```java
@Enumerated(EnumType.STRING)
private ReservationStatus status;
```

## 8. Column Rule

Use column constraints when useful.

Example:

```java
@Column(nullable = false, length = 100)
private String title;
```

## 9. Production Schema Rule

Do not rely on this in production:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

For production, prefer:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

Or use migration tools such as Flyway or Liquibase.

## 10. Entity Checklist

- [ ] Entity has clear responsibility.
- [ ] Required fields are non-null.
- [ ] Long text fields have reasonable length.
- [ ] Relationship fetch type is reviewed.
- [ ] Cascade settings are reviewed.
- [ ] Enum fields use `EnumType.STRING`.
- [ ] DTOs are used for API responses.
- [ ] Entity is not returned directly.
- [ ] Migration risk is reviewed.