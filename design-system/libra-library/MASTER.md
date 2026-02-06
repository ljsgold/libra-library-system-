# Libra Library Design System - Apple Style

> **Design Philosophy:** Minimal, Clean, Elegant - Inspired by Apple's Human Interface Guidelines

## Global Rules

### Color Palette (Apple Style)

| Role | Hex | CSS Variable |
|------|-----|--------------|
| Primary | `#007AFF` | `--color-primary` |
| Primary Light | `#5AC8FA` | `--color-primary-light` |
| Secondary | `#86868B` | `--color-secondary` |
| Success | `#34C759` | `--color-success` |
| Warning | `#FF9500` | `--color-warning` |
| Danger | `#FF3B30` | `--color-danger` |
| Background | `#FFFFFF` | `--color-background` |
| Background Secondary | `#F5F5F7` | `--color-background-secondary` |
| Surface | `#FFFFFF` | `--color-surface` |
| Text | `#1D1D1F` | `--color-text` |
| Text Secondary | `#86868B` | `--color-text-secondary` |
| Border | `rgba(0, 0, 0, 0.08)` | `--color-border` |

### Typography

- **Font Family:** Inter, -apple-system, BlinkMacSystemFont
- **Google Fonts:** `https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap`

#### Type Scale

| Element | Size | Weight | Letter Spacing |
|---------|------|--------|----------------|
| H1 | 48px | 700 | -0.003em |
| H2 | 32px | 600 | -0.02em |
| H3 | 24px | 600 | -0.02em |
| Body | 17px | 400 | -0.022em |
| Small | 14px | 400 | 0 |
| Caption | 12px | 500 | 0 |

### Shadows

| Level | Value |
|-------|-------|
| sm | `0 1px 3px rgba(0, 0, 0, 0.06), 0 1px 2px rgba(0, 0, 0, 0.04)` |
| md | `0 4px 6px rgba(0, 0, 0, 0.04), 0 2px 4px rgba(0, 0, 0, 0.06)` |
| lg | `0 10px 20px rgba(0, 0, 0, 0.04), 0 6px 6px rgba(0, 0, 0, 0.05)` |
| xl | `0 25px 50px rgba(0, 0, 0, 0.08), 0 12px 24px rgba(0, 0, 0, 0.06)` |

### Border Radius

| Size | Value |
|------|-------|
| Small | 8px |
| Medium | 12px |
| Large | 16px |
| XL | 18px |
| 2XL | 20px |
| Full | 999px |

## Animation Guidelines

- **Duration:** 200-300ms for micro-interactions
- **Easing:** `cubic-bezier(0.25, 0.1, 0.25, 1)` (Apple standard)
- **Hover Transform:** `translateY(-4px)` for cards
- **Accessibility:** Always respect `prefers-reduced-motion`

```css
@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}
```

## Component Specifications

### Cards
- Border radius: 18px
- Background: White with subtle shadow
- Hover: Scale 1.01 + shadow-lg
- Padding: 24px

### Buttons
- Border radius: 12px
- Primary: Apple Blue (#007AFF)
- Height: 44-48px for main actions
- Font weight: 600
- Hover: Subtle lift + shadow

### Input Fields
- Border radius: 12px
- Background: `#F5F5F7` (secondary)
- Focus: White bg + blue ring
- Padding: 12px 16px

### Tables
- Header: Light gray background
- Border: Subtle, light
- Row hover: Slight background change

## Accessibility

- WCAG AAA compliance for text contrast
- Minimum 4.5:1 contrast ratio
- Clear focus states (4px blue ring)
- Keyboard navigation support

## Pre-Delivery Checklist

- [ ] No emojis as icons (use SVG: Heroicons/Lucide)
- [ ] cursor-pointer on all clickable elements
- [ ] Hover states with smooth transitions (200-300ms)
- [ ] Light mode: text contrast 4.5:1 minimum
- [ ] Focus states visible for keyboard nav
- [ ] prefers-reduced-motion respected
- [ ] Responsive: 375px, 768px, 1024px, 1440px
