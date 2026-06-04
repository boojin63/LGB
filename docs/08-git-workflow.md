# Git Workflow Template

## 1. Purpose

This document defines how Git should be used in this project.

The goal is to keep development safe, organized, and easy to review.

## 2. Branch Strategy

Use small and clear branches for each task.

### Main Branches

| Branch | Purpose |
|---|---|
| `main` | Stable production-ready code |
| `develop` | Integrated development branch |
| `feature/*` | New feature work |
| `fix/*` | Bug fixes |
| `refactor/*` | Code restructuring |
| `docs/*` | Documentation changes |
| `chore/*` | Setup, config, tooling changes |
| `release/*` | Release preparation |

## 3. Branch Naming Rules

Use lowercase English words and hyphens.

### Examples

```text
feature/user-login
feature/reservation-system
fix/auth-token-refresh
fix/api-error-handling
refactor/user-service
docs/update-readme
chore/setup-eslint
release/v1.0.0
```

## 4. Commit Message Rules

Use clear commit messages.

Recommended format:

```text
type: short description
```

### Commit Types

| Type | Meaning |
|---|---|
| `feat` | New feature |
| `fix` | Bug fix |
| `refactor` | Code restructuring without behavior change |
| `docs` | Documentation only |
| `style` | Formatting or UI style change |
| `test` | Test code |
| `chore` | Config, dependency, tooling |
| `build` | Build or deployment related |
| `security` | Security fix |

### Examples

```text
feat: add user login api
fix: resolve logout button issue
docs: update project setup guide
refactor: simplify auth middleware
chore: setup prettier config
security: protect admin route
```

## 5. Work Process

For each task:

1. Create a new branch.
2. Make small changes.
3. Test locally.
4. Commit with a clear message.
5. Open a pull request.
6. Review the checklist.
7. Merge only after passing checks.

## 6. Recommended Command Flow

```bash
git checkout main
git pull origin main
git checkout -b feature/example-feature
```

After work:

```bash
git status
git add .
git commit -m "feat: add example feature"
git push origin feature/example-feature
```

## 7. Pull Request Rules

Before opening a PR:

- [ ] The task is small enough to review.
- [ ] The code builds locally.
- [ ] The main feature works.
- [ ] Existing features are not broken.
- [ ] Documentation is updated if needed.
- [ ] Environment variables are not committed.
- [ ] No unnecessary files are included.

## 8. Merge Rules

A branch can be merged only when:

- [ ] PR description is written.
- [ ] Code review checklist is completed.
- [ ] CI check passes.
- [ ] QA checklist is completed.
- [ ] Security impact is checked.
- [ ] The change is approved by the project owner.

## 9. Rollback Rule

If a merged change breaks production:

1. Identify the breaking commit.
2. Revert the commit.
3. Check build and runtime logs.
4. Create a fix branch.
5. Re-test the affected feature.
6. Document the incident.

Example:

```bash
git revert <commit-hash>
```

## 10. Git Safety Rules

- Do not commit `.env` files.
- Do not commit secrets or API keys.
- Do not commit `node_modules`.
- Do not commit generated build folders unless required.
- Do not force push to `main`.
- Do not merge untested code.
- Do not mix unrelated changes in one commit.