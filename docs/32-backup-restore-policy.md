# Backup / Restore Policy Template

## 1. Purpose

This document defines the backup and restore policy for Spring Boot + MySQL projects.

The goal is to prevent permanent data loss and make recovery possible after incidents.

## 2. Why Backup Policy Matters

Without backup:

```text
Database is deleted.
Migration fails.
User data is corrupted.
No recovery is possible.
```

With backup:

```text
Database can be restored.
Incident damage is reduced.
Deployment risk becomes lower.
```

## 3. Backup Targets

Back up:

- MySQL database
- Production `.env` values or secret configuration reference
- Uploaded files if the service has file upload
- Important configuration files
- Deployment scripts

Do not store secrets in unsafe locations.

## 4. Backup Frequency

Recommended minimum:

| Environment | Frequency |
|---|---|
| Local | Optional |
| Staging | Daily or before major test |
| Production | Daily |
| Before destructive migration | Mandatory |

For early MVP:

```text
Daily production backup + manual backup before risky migration
```

## 5. Retention Policy

Recommended:

| Backup Type | Retention |
|---|---|
| Daily backup | 7-14 days |
| Weekly backup | 4-8 weeks |
| Monthly backup | 3-12 months |

Adjust based on data importance and cost.

## 6. MySQL Backup Command

Example:

```bash
mysqldump -h DB_HOST -u DB_USERNAME -p DB_NAME > backup_YYYYMMDD.sql
```

With Docker:

```bash
docker exec CONTAINER_NAME mysqldump -u root -p DB_NAME > backup_YYYYMMDD.sql
```

## 7. MySQL Restore Command

Example:

```bash
mysql -h DB_HOST -u DB_USERNAME -p DB_NAME < backup_YYYYMMDD.sql
```

With Docker:

```bash
docker exec -i CONTAINER_NAME mysql -u root -p DB_NAME < backup_YYYYMMDD.sql
```

## 8. Backup Before Migration

Before destructive or risky migration:

1. Stop or reduce write traffic if needed.
2. Create DB backup.
3. Verify backup file exists.
4. Store backup safely.
5. Apply migration.
6. Verify application works.
7. Keep backup until release is stable.

## 9. Restore Drill

A backup is not reliable until restore is tested.

Recommended routine:

```text
At least once before production launch.
Then periodically for important services.
```

Restore drill checklist:

- [ ] Backup file exists.
- [ ] Restore command works.
- [ ] Restored DB has expected tables.
- [ ] Application can connect to restored DB.
- [ ] Main user flow works after restore.

## 10. Security Rules

- Do not commit backup files.
- Do not upload backups to public storage.
- Encrypt backups when possible.
- Limit backup access.
- Do not share production DB dumps casually.
- Remove old backups according to retention policy.

## 11. Incident Restore Procedure

When data restore is needed:

1. Stop affected service if continuing writes can make damage worse.
2. Identify latest safe backup.
3. Document data loss window.
4. Restore to temporary DB first if possible.
5. Verify restored data.
6. Switch application to restored DB or restore production DB.
7. Run smoke test.
8. Document incident.

## 12. Checklist

- [ ] Backup target is defined.
- [ ] Backup frequency is defined.
- [ ] Retention period is defined.
- [ ] Backup storage is safe.
- [ ] Restore command is documented.
- [ ] Restore drill was tested.
- [ ] Backup before risky migration is required.
- [ ] Backup files are excluded from Git.

## 13. Codex Rule

When Codex changes production database or deployment strategy:

1. Check whether backup is needed.
2. Recommend backup before destructive migration.
3. Never commit backup files.
4. Document restore steps.
5. Mention possible data loss window.