# Incident Response Template

## 1. Purpose

This document defines how to respond when a production issue or serious bug occurs.

The goal is to reduce damage, fix the issue safely, and prevent recurrence.

## 2. Incident Severity

| Level | Meaning | Example | Response |
|---|---|---|---|
| Critical | Service unusable or data/security issue | Login broken, data leak | Immediate fix |
| High | Major feature broken | Main CRUD broken | Fix urgently |
| Medium | Important issue with workaround | Search broken | Fix soon |
| Low | Minor issue | UI spacing issue | Backlog |

## 3. First Response Steps

When an incident occurs:

1. Stop making unrelated changes.
2. Identify affected users/features.
3. Check recent deployments.
4. Check frontend logs.
5. Check backend logs.
6. Check database status.
7. Reproduce the issue if possible.
8. Decide rollback or hotfix.
9. Document the incident.

## 4. Incident Report

### Incident Title

```text

```

### Date / Time

```text

```

### Severity

```text
Critical / High / Medium / Low
```

### Affected Area

- [ ] Frontend
- [ ] Backend
- [ ] Database
- [ ] Authentication
- [ ] Authorization
- [ ] Payment
- [ ] Deployment
- [ ] Other:

### What Happened?

```text

```

### User Impact

```text

```

### Root Cause

```text

```

### Immediate Fix

```text

```

### Long-Term Prevention

```text

```

## 5. Rollback Decision

Rollback needed?

```text
YES / NO
```

Reason:

```text

```

Rollback steps:

1.
2.
3.

## 6. Hotfix Process

If hotfix is needed:

1. Create a fix branch.
2. Reproduce the issue.
3. Make the smallest safe fix.
4. Test affected flow.
5. Test related existing flow.
6. Review security impact.
7. Deploy.
8. Monitor logs after deployment.

Branch example:

```text
fix/critical-login-error
```

Commit example:

```text
fix: resolve critical login error
```

## 7. Post-Incident Checklist

- [ ] Incident cause documented.
- [ ] Fix deployed.
- [ ] Logs checked after deployment.
- [ ] User flow verified.
- [ ] Related tests added or updated if possible.
- [ ] Prevention action added to backlog.
- [ ] Release checklist updated if needed.

## 8. Codex Incident Prompt

Use this prompt when asking Codex to analyze an incident:

```text
아래 운영 장애를 준전문 개발사 체계로 분석해줘.

아직 코드는 수정하지 마.

다음 관점으로 분석해줘.

1. QA Agent: 재현 조건과 사용자 영향
2. Frontend Agent: 화면/상태/API 호출 문제
3. Backend Agent: API/서버 로직 문제
4. Database Agent: DB/마이그레이션/데이터 문제
5. Security Agent: 인증/권한/정보 노출 위험
6. DevOps Agent: 배포/환경변수/서버 로그 문제

마지막에는 Orchestrator Agent가 아래 형식으로 정리해줘.

1. 장애 요약
2. 심각도
3. 가장 가능성 높은 원인
4. 확인해야 할 파일
5. 즉시 조치
6. 근본 해결책
7. 재발 방지책
8. 테스트 방법
```