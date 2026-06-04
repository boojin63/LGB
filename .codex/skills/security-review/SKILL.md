---
name: security-review
description: Use this skill when reviewing authentication, authorization, data exposure, and API safety.
---

# Security Review Skill

## Goal

Find security risks before release.

## Required Checks

1. Authentication required where needed.
2. Authorization enforced by role.
3. Users cannot access other users' private data.
4. Admin-only APIs are protected.
5. Request body is validated.
6. Sensitive data is not returned.
7. Secrets are not committed.
8. Environment variables are used correctly.
9. Error responses do not leak internals.

## Output Format

1. Security summary
2. Authentication issues
3. Authorization issues
4. Data exposure issues
5. Input validation issues
6. Secret management issues
7. Must-fix issues
8. Recommended fixes
9. Final security status