# Database Schema Template

## 1. Database Overview

Database:

```text

```

ORM:

```text

```

Example:

```text
MySQL + Prisma
```

## 2. Naming Rules

Recommended rules:

- Table/model names should be clear.
- Field names should be predictable.
- Use `createdAt` and `updatedAt` for important models.
- Use enum values for status fields when possible.
- Avoid unnecessary nullable fields.
- Avoid destructive schema changes after data exists.

## 3. Core Models

### User

Purpose:

```text

```

Fields:

| Field | Type | Required | Unique | Description |
|---|---|---:|---:|---|
| id | String / Int | Yes | Yes | Primary key |
| email | String | Yes | Yes | User email |
| password | String | Yes | No | Hashed password |
| name | String | Yes | No | User name |
| role | String / Enum | Yes | No | User role |
| createdAt | DateTime | Yes | No | Created time |
| updatedAt | DateTime | Yes | No | Updated time |

Notes:

```text

```

---

### Model Template

Model name:

```text

```

Purpose:

```text

```

Fields:

| Field | Type | Required | Unique | Description |
|---|---|---:|---:|---|
| id | String / Int | Yes | Yes | Primary key |
|  |  |  |  |  |
| createdAt | DateTime | Yes | No | Created time |
| updatedAt | DateTime | Yes | No | Updated time |

Notes:

```text

```

## 4. Relationships

| Model A | Relation | Model B | Description |
|---|---|---|---|
| User | has many |  |  |
|  | belongs to | User |  |

Relationship notes:

```text

```

## 5. Enums

Define enum values if needed.

### Role

```text
USER
ADMIN
```

### Status

```text
PENDING
APPROVED
REJECTED
CANCELLED
```

Custom enums:

```text

```

## 6. Indexes

Recommended indexes:

- Unique fields
- Foreign key fields
- Frequently filtered status fields
- Frequently searched date fields
- Frequently searched category fields

Index plan:

| Model | Field | Reason |
|---|---|---|
| User | email | Login lookup |
|  |  |  |

## 7. Prisma Schema Draft

Use this area for draft schema.

```prisma
model User {
  id        Int      @id @default(autoincrement())
  email     String   @unique
  password  String
  name      String
  role      String   @default("USER")
  createdAt DateTime @default(now())
  updatedAt DateTime @updatedAt
}
```

Additional models:

```prisma

```

## 8. Migration Notes

Before migration, check:

- [ ] Will existing data be deleted?
- [ ] Are required fields being added?
- [ ] Are default values needed?
- [ ] Are relationships nullable or required?
- [ ] Is rollback possible?
- [ ] Does seed data need to be updated?

Notes:

```text

```

## 9. Seed Data

Initial data needed for development:

| Data | Purpose |
|---|---|
| Admin user | Admin testing |
| Test user | Normal user testing |
| Sample item | Feature testing |

Seed notes:

```text

```

## 10. Data Security Notes

Check:

- [ ] Passwords are hashed.
- [ ] Sensitive fields are not returned to frontend.
- [ ] Users cannot access other users' private data.
- [ ] Admin-only data is protected.
- [ ] Deleted data handling is clear.

Notes:

```text

```