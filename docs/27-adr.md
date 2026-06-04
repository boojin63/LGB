# ADR Template

## 1. Purpose

ADR means Architecture Decision Record.

An ADR documents important technical decisions.

The goal is to remember why a decision was made, not just what was chosen.

## 2. Why ADR Matters

Without ADR:

```text
We use JWT, but nobody remembers why.
We use MySQL, but nobody knows the reason.
We chose JPA, but later developers do not understand the tradeoff.
```

With ADR:

```text
Important decisions are documented.
Future developers can understand the context.
Technical choices are easier to review.
```

## 3. ADR Folder

Recommended folder:

```text
docs/adr/
```

Recommended files:

```text
0000-adr-template.md
0001-use-spring-boot.md
0002-use-mysql.md
0003-use-jpa.md
0004-use-jwt-auth.md
```

## 4. ADR Naming Rule

Format:

```text
NNNN-short-title.md
```

Examples:

```text
0001-use-spring-boot.md
0002-use-mysql.md
0003-use-jpa.md
0004-use-jwt-auth.md
```

## 5. ADR Status

Common status values:

```text
Proposed
Accepted
Deprecated
Superseded
```

## 6. When to Write ADR

Write ADR for:

- Framework choice
- Database choice
- Authentication strategy
- Deployment strategy
- Major architecture pattern
- Major security decision
- Major external service decision

## 7. ADR Checklist

- [ ] Decision is important enough to document.
- [ ] Context is explained.
- [ ] Options are listed.
- [ ] Final decision is clear.
- [ ] Consequences are honest.
- [ ] Status is written.
- [ ] Date is written.

## 8. Codex Rule

When Codex makes or recommends a major architecture decision:

1. Suggest writing an ADR.
2. Explain why the decision matters.
3. List alternatives.
4. Document tradeoffs.
5. Keep the ADR short and practical.