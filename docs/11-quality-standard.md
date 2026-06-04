# Quality Standard Template

## 1. Purpose

This document defines the quality standards for this project.

The goal is to make sure the project is maintainable, safe, understandable, and release-ready.

## 2. General Quality Principles

All work should follow these principles:

1. Solve the actual user problem.
2. Keep the MVP scope small.
3. Prefer simple solutions.
4. Avoid overengineering.
5. Avoid hardcoded production data.
6. Keep code readable for a beginner developer.
7. Do not break existing working features.
8. Test before considering work complete.
9. Document important decisions.
10. Review security before release.

## 3. Product Quality Standard

A feature is product-ready only when:

- [ ] The user problem is clear.
- [ ] The feature has a clear purpose.
- [ ] The feature is part of the MVP or approved roadmap.
- [ ] The feature does not add unnecessary complexity.
- [ ] Success criteria are defined.

## 4. UX Quality Standard

A screen is UX-ready only when:

- [ ] The main action is obvious.
- [ ] The user knows what to do next.
- [ ] Empty state is helpful.
- [ ] Error state explains the problem.
- [ ] Success state confirms the action.
- [ ] Destructive actions require confirmation.
- [ ] Mobile usage is considered.

## 5. UI Quality Standard

UI is acceptable only when:

- [ ] Spacing is consistent.
- [ ] Typography is readable.
- [ ] Buttons are visually clear.
- [ ] Forms are understandable.
- [ ] Components look consistent.
- [ ] Color usage is not confusing.
- [ ] The design does not block usability.

## 6. Frontend Quality Standard

Frontend code is acceptable only when:

- [ ] It uses real API data unless mock data is explicitly approved.
- [ ] Loading state is handled.
- [ ] Empty state is handled.
- [ ] Error state is handled.
- [ ] Success state is handled.
- [ ] Components are reasonably small.
- [ ] State logic is understandable.
- [ ] API paths match backend routes.
- [ ] No unnecessary libraries are added.
- [ ] No avoidable console errors remain.

## 7. Backend Quality Standard

Backend code is acceptable only when:

- [ ] API routes are predictable.
- [ ] Request bodies are validated.
- [ ] Authentication is checked where needed.
- [ ] Authorization is checked where needed.
- [ ] Business logic is clear.
- [ ] Error responses are consistent.
- [ ] Sensitive data is not returned.
- [ ] Database access is safe and understandable.
- [ ] No hardcoded production data is used.

## 8. Database Quality Standard

Database design is acceptable only when:

- [ ] Models have clear purposes.
- [ ] Relationships are correct.
- [ ] Required fields are justified.
- [ ] Nullable fields are intentional.
- [ ] Indexes are added for real lookup needs.
- [ ] Migration risk is reviewed.
- [ ] Destructive changes are approved.
- [ ] Seed data is safe and non-sensitive.

## 9. Security Quality Standard

A change is security-acceptable only when:

- [ ] Secrets are not committed.
- [ ] Environment variables are used.
- [ ] Passwords are hashed.
- [ ] JWT or session handling is safe.
- [ ] Admin APIs are protected.
- [ ] Users cannot access other users' private data.
- [ ] Request input is validated.
- [ ] Sensitive data is not returned.
- [ ] Error messages do not expose internals.

## 10. QA Quality Standard

A feature is QA-ready only when:

- [ ] Main success flow is tested.
- [ ] Main failure flow is tested.
- [ ] Invalid input is tested.
- [ ] Empty state is tested.
- [ ] Permission failure is tested.
- [ ] Existing related features are checked.
- [ ] No critical issue remains.

## 11. Deployment Quality Standard

Deployment is acceptable only when:

- [ ] Build succeeds.
- [ ] Environment variables are configured.
- [ ] Database migration is reviewed.
- [ ] Production API URL is correct.
- [ ] CORS is configured.
- [ ] Smoke test is completed.
- [ ] Rollback method is known.

## 12. Severity Levels

Use these severity levels for bugs and issues.

| Level | Meaning | Example | Release Impact |
|---|---|---|---|
| Critical | Service cannot be used or data is exposed | Login broken, data leak | Must block release |
| High | Major feature broken | Payment or main CRUD broken | Usually block release |
| Medium | Important but workaround exists | Search broken | Fix soon |
| Low | Minor issue | Small UI spacing issue | Can release |
| Improvement | Enhancement | Better empty state copy | Backlog |

## 13. Release Quality Gate

A release can proceed only when:

- [ ] No critical issue remains.
- [ ] No unapproved high issue remains.
- [ ] Main user flow works.
- [ ] Auth and permission checks work.
- [ ] Production environment is configured.
- [ ] Rollback plan exists.