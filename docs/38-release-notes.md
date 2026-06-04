# Release Notes Template

## 1. Purpose

This document defines the release notes format for Spring Boot projects.

The goal is to make every release understandable, traceable, and easier to rollback if needed.

## 2. Recommended File

Recommended location:

```text
RELEASE_NOTES.md
```

Or for template storage:

```text
code-templates/spring/release/RELEASE_NOTES_TEMPLATE.md
```

## 3. Release Note Format

```md
# Release Notes

## v0.1.0 - YYYY-MM-DD

### Added

- 

### Changed

- 

### Fixed

- 

### Security

- 

### Database

- 

### Deployment

- 

### Known Issues

- 

### Rollback Notes

- 
```

## 4. Version Naming

Recommended early-stage versioning:

```text
v0.1.0
v0.2.0
v0.3.0
v1.0.0
```

Simple rule:

| Change Type | Version Example |
|---|---|
| Small fix | `v0.1.1` |
| New MVP feature | `v0.2.0` |
| First stable release | `v1.0.0` |

## 5. What to Include

Include:

- New features
- Bug fixes
- Security changes
- DB migration changes
- Deployment changes
- Breaking changes
- Known issues
- Rollback notes

## 6. Database Change Rule

If release includes Flyway migration, document:

```text
Migration files:
- V2__create_posts_table.sql
- V3__add_user_status.sql

Risk:
- Adds new columns only.
- No destructive change.
```

## 7. Security Change Rule

If release includes auth/security changes, document:

```text
Security:
- Admin APIs now require ADMIN role.
- JWT expiration changed to 24 hours.
```

## 8. Rollback Rule

Each release should include rollback notes.

Example:

```text
Rollback:
- Revert Docker image to previous tag.
- Do not rollback DB migration unless restore plan is confirmed.
```

## 9. Checklist

- [ ] Release version is written.
- [ ] Release date is written.
- [ ] Added features are listed.
- [ ] Fixed bugs are listed.
- [ ] DB migrations are listed.
- [ ] Security changes are listed.
- [ ] Deployment changes are listed.
- [ ] Known issues are listed.
- [ ] Rollback notes are written.

## 10. Codex Rule

When Codex prepares a release:

1. Update release notes.
2. Mention DB migrations.
3. Mention security changes.
4. Mention deployment changes.
5. Mention rollback notes.
6. Keep release notes clear and short.