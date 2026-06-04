# GitHub Actions Deployment Workflow Template

## 1. Purpose

This document defines deployment workflow templates for Spring Boot projects using GitHub Actions.

The goal is to make deployment repeatable, reviewable, and safer.

## 2. Included Workflow Templates

```text
.github/workflows/deploy-render.yml
.github/workflows/deploy-docker-server.yml
```

## 3. Deployment Types

### Render Deploy

Use this when the project is deployed through a platform that supports deploy hooks.

Typical flow:

```text
Push to main
↓
Run build/test
↓
Call deploy hook
↓
Platform deploys application
```

### Docker Server Deploy

Use this when deploying to a server through SSH and Docker Compose.

Typical flow:

```text
Push to main
↓
Run build/test
↓
Build Docker image
↓
Push image to registry
↓
SSH into server
↓
Pull new image
↓
Restart container
```

## 4. Required GitHub Secrets

### For Render-style deploy hook

```text
RENDER_DEPLOY_HOOK_URL
```

### For Docker server deploy

```text
DOCKERHUB_USERNAME
DOCKERHUB_TOKEN
SERVER_HOST
SERVER_USER
SERVER_SSH_KEY
SERVER_APP_DIR
```

Application secrets should usually live on the server or deployment platform, not directly in the workflow.

## 5. Deployment Safety Rules

- Do not deploy without build passing.
- Do not print secrets.
- Use GitHub Secrets.
- Keep production `.env` on the server or platform.
- Keep rollback strategy documented.
- Avoid deploying from random branches.
- Prefer deployment from `main` or release branches.

## 6. Manual Deployment Trigger

Use `workflow_dispatch` so deployment can be run manually when needed.

Example:

```yaml
on:
  workflow_dispatch:
```

## 7. Checklist

- [ ] Build step exists.
- [ ] Test step exists.
- [ ] Deployment only runs from approved branch.
- [ ] Secrets are stored in GitHub Secrets.
- [ ] Production environment variables are not committed.
- [ ] Rollback strategy exists.
- [ ] Deployment logs can be checked.

## 8. Codex Rule

When Codex adds deployment workflow:

1. Ask or infer the target deployment platform.
2. Use GitHub Secrets for sensitive values.
3. Keep build/test before deploy.
4. Avoid hardcoding production secrets.
5. Explain how to configure secrets.
6. Explain rollback.