# Design System Template

## 1. Visual Direction

Choose the product's visual style.

Examples:

- Modern and minimal
- Friendly and casual
- Professional SaaS
- Mobile-first utility
- Premium and clean
- Playful and social
- Data-focused dashboard

Selected direction:

```text

```

## 2. Brand Keywords

Write 3 to 5 keywords that describe the product's feeling.

Examples:

- Trustworthy
- Simple
- Fast
- Friendly
- Professional

Keywords:

1.
2.
3.
4.
5.

## 3. Color System

### Primary Color

| Token | Value | Usage |
|---|---|---|
| primary |  | Main buttons, links |
| primary-hover |  | Button hover |
| primary-light |  | Light background |
| primary-dark |  | Strong emphasis |

### Neutral Colors

| Token | Value | Usage |
|---|---|---|
| background |  | Page background |
| surface |  | Card/modal background |
| border |  | Lines and dividers |
| text-primary |  | Main text |
| text-secondary |  | Sub text |
| text-muted |  | Placeholder/helper text |

### State Colors

| Token | Value | Usage |
|---|---|---|
| success |  | Success message |
| warning |  | Warning message |
| error |  | Error message |
| info |  | Information message |

## 4. Typography

Recommended structure:

| Element | Size | Weight | Usage |
|---|---:|---:|---|
| Page title | 24-32px | 700 | Main page heading |
| Section title | 20-24px | 600 | Section heading |
| Card title | 16-20px | 600 | Card heading |
| Body text | 14-16px | 400 | Normal text |
| Caption | 12-13px | 400 | Helper text |
| Button text | 14-16px | 600 | Button label |

Selected font:

```text

```

## 5. Layout Rules

General layout rules:

- Use consistent spacing.
- Prefer card-based sections.
- Keep forms simple.
- Use clear visual hierarchy.
- Design mobile-first if applicable.
- Avoid clutter.
- Keep primary actions obvious.

Spacing scale:

| Token | Value |
|---|---|
| xs | 4px |
| sm | 8px |
| md | 16px |
| lg | 24px |
| xl | 32px |
| 2xl | 48px |

## 6. Components

Required reusable components:

- [ ] Button
- [ ] Input
- [ ] Select
- [ ] Textarea
- [ ] Checkbox
- [ ] Radio
- [ ] Card
- [ ] Modal
- [ ] Toast
- [ ] Badge
- [ ] Avatar
- [ ] Tabs
- [ ] Dropdown
- [ ] EmptyState
- [ ] LoadingSpinner
- [ ] ErrorMessage
- [ ] ConfirmDialog

## 7. Button Rules

Button variants:

| Variant | Usage |
|---|---|
| Primary | Main action |
| Secondary | Sub action |
| Outline | Low-priority action |
| Ghost | Minimal action |
| Danger | Delete/destructive action |

Rules:

- One primary button per main section.
- Destructive actions must be visually distinct.
- Important destructive actions need confirmation.

## 8. Form Rules

Required form states:

- Default
- Focus
- Disabled
- Error
- Success
- Loading/submitting

Form guidance:

- Labels must be clear.
- Required fields must be obvious.
- Error messages must explain how to fix the issue.
- Long forms should be divided into sections.

## 9. UI States

Each important screen should handle:

- [ ] Loading state
- [ ] Empty state
- [ ] Error state
- [ ] Success state
- [ ] Permission denied state
- [ ] Offline/server error state

## 10. Responsive Rules

Breakpoints:

| Device | Width |
|---|---:|
| Mobile | 0-767px |
| Tablet | 768-1023px |
| Desktop | 1024px+ |

Rules:

- Mobile layout should be single-column.
- Desktop layout can use two-column or dashboard layout.
- Important actions should remain accessible on mobile.

## 11. Accessibility

Accessibility checklist:

- [ ] Text is readable.
- [ ] Buttons have clear labels.
- [ ] Form errors are visible.
- [ ] Color is not the only way to show meaning.
- [ ] Interactive elements have enough touch area.
- [ ] Important actions have confirmation.