# DroidQuest — Design System Specification (`DESIGN.md`)

Exported and refined from Google Stitch project **DroidQuest Mobile Redesign** (`projects/5313316087187846071`, screen `63f6a10c0d1a4e28a3363c987522f021` — *Minimal Monochrome Refinement*).

---

## 1. Visual Philosophy: 90% Monochrome Neutral + 1 Primary Accent

DroidQuest is a focused 100-day Senior Android interview preparation companion. The UI prioritizes effortless data scannability over arcade decoration:
- **Calm, layered dark neutrals** carry 90% of the screen.
- **One disciplined primary accent — Emerald (`#22C55E`)** — guides the eye exclusively to progress bars, active/completed states, the active navigation tab, and the single primary CTA button on each screen.
- **Zero rainbow noise:** telemetry pills, pillar breakdowns, review alerts, resource tags, and zone badges use uniform monochrome surfaces and hairline borders rather than competing neon hues.
- **Concise density:** merge redundant headers, eliminate decorative filler copy, and surface core metrics (`Day X/100`, streak, XP, overall %, next quest, due reviews) immediately.

---

## 2. Color Tokens

### Surface Scale — Dark Mode (Charcoal-Obsidian Depth)
| Role | Token | Hex | Usage |
| :--- | :--- | :--- | :--- |
| Canvas / Background | `ObsidianCanvas` | `#0B0E14` | Root screen background |
| Recessed Input / Well | `ObsidianLowest` | `#0E131D` | Text fields, search bar, recessed prompt boxes, progress tracks |
| Quiet Secondary Card | `ObsidianLow` | `#111620` | Collapsed sections, upcoming rows, locked trophies |
| Primary Card Surface | `ObsidianContainer` | `#131822` | Hero card, review banner, trail quest cards, floating nav pill |
| Elevated Focal Card | `ObsidianHigh` | `#181F2C` | Next Quest card, active trail target, tonal buttons, dialogs |
| Highest Surface | `ObsidianHighest` | `#20293A` | Active neutral chip fill, hover/pressed highlights |

### Surface Scale — Light Mode (Paper-Slate Depth, Stitch Screen `21a75fad759940bcb5317510273433c0`)
| Role | Token | Hex | Usage |
| :--- | :--- | :--- | :--- |
| Canvas / Background | `PaperCanvas` | `#F8FAFC` | Soft cool off-white root canvas |
| Recessed Input / Well | `PaperLowest` | `#F1F5F9` | Search bar, progress tracks, tonal buttons, inactive pills |
| Quiet Secondary Card | `PaperLow` | `#F4F6FA` | Collapsed sections, subtle panels |
| Primary / Focal Card | `PaperContainer` | `#FFFFFF` | Pure white cards & floating pill nav with `1dp` hairline borders |
| Primary Ink | `LightInkPrimary` | `#0F172A` | Deep charcoal headings & tabular KPIs |
| Secondary / Muted Ink | `LightInkMuted` | `#64748B` | Eyebrows, subtitles, inactive nav items |
| Hairline Borders | `LightHairlineSubtle` / `Strong` | `#E2E8F0` / `#CBD5E1` | Crisp card borders & floating nav border |
| Primary Accent (Light) | `LightEmerald` | `#16A34A` | High-contrast emerald CTAs (white text), progress bars, active nav |

### Typography & Hairline Borders
| Role | Token | Hex | Usage |
| :--- | :--- | :--- | :--- |
| Primary Ink | `InkPrimary` | `#F4F6FA` | Headings, KPI figures, primary labels |
| Secondary Ink | `InkSecondary` | `#B8C1D1` | Body descriptions, interview answers |
| Muted Ink | `InkMuted` | `#8A94A6` | Eyebrows, subtitles, inactive nav items, timestamps |
| Subtle Ink | `InkSubtle` | `#5B6577` | Placeholders, locked states, dividers |
| Hairline Subtle | `HairlineSubtle` | `#1E2634` | Card borders, list dividers |
| Hairline Strong | `HairlineStrong` | `#2B3548` | Elevated card borders, floating nav border, tonal buttons |

### Single Primary Accent & Restrained Semantic Roles
| Role | Token | Hex | Usage |
| :--- | :--- | :--- | :--- |
| Primary Accent | `DroidEmerald` | `#22C55E` | Primary CTA fill, progress bars, checkmarks, active nav icon/label |
| On Primary | `DroidEmeraldDeep` | `#051F10` | High-contrast dark ink on solid Emerald buttons |
| Primary Container | `DroidEmeraldContainer` | `#0E2419` | Subtle emerald tint for unlocked/active badges |
| Primary Border | `DroidEmeraldBorder` | `#22C55E` (35–45% alpha) | Active zone pill, active quest card |
| Danger / Destructive | `QuietCrimson` | `#E55365` | Destructive reset confirmation & subtle "Forgot" cue only |

---

## 3. Typography

- **Display / Headings:** `Nunito` (Bold / ExtraBold) — crisp, clean geometric sans-serif with tight negative tracking (`-0.2sp` to `-0.4sp`) for modern mobile clarity.
- **Body / UI Labels:** `Nunito` (Regular / SemiBold / Bold) — high legibility for dense technical subtopics and Senior interview answers.
- **Tabular Metrics & Metadata:** `JetBrains Mono` (`fontFeatureSettings = "tnum"`) — used for `34%`, `Day 18/100`, `1,450 XP`, `11/17`, `2/4`, and spaced-repetition intervals (`1d → 3d → 7d → 21d → 60d`).

---

## 4. Shape & Spacing Scale

- **Screen horizontal margin:** `16.dp` (max content width `1040.dp` on wide desktop/tablet).
- **Card padding:** `14.dp–18.dp`, vertical section spacing `14.dp–18.dp`.
- **Corner Radii:**
  - `MicroBadge` / tags / heatmap tiles: `8.dp`
  - `TactileButton` / inputs / rating tiles: `12.dp`
  - `HudPill` / telemetry pills / zone pills: `12.dp` or `CircleShape`
  - `QuestCard` / primary cards: `16.dp`
  - `ZoneBanner` / hero card / modal cards: `20.dp`
  - `SheetTop`: `24.dp` top corners
  - `FloatingNavPill`: `CircleShape` (`28.dp` height-adaptive pill)

---

## 5. Core Layout & Navigation Architecture

1. **Compact Single-Row Top Bar:** Minimal DroidQuest emblem + `DroidQuest` + `Lv.X · Rank` inline, with subtle ghost Sound & Settings buttons on right.
2. **Unified Telemetry Strip:** 3 equal-width neutral pills (`Day X/100`, `Xd streak`, `X XP`) directly below the header on mobile (inline on wide screens).
3. **Hero Readiness Card:** `100-DAY SPRINT` eyebrow + `Xd left · X/day` mono metadata, bold `X%` + `done of total topics mastered`, thin `6.dp` emerald progress bar, and a 4-column monochrome pillar breakdown (`Android`, `DSA`, `Sys Des`, `Career`).
4. **Priority Actions (Stacked):**
   - Calm 1-row `X Reviews Due` neutral card with tonal `Review →` button (hidden or calm empty state when 0 due).
   - Elevated `Next Quest` card with concise metadata, title, 1-line subtitle, and the ONE solid Emerald `Continue Quest →` CTA.
5. **Adventure Trail:** Unified `ZONES` + `High-Yield` toggle row, horizontal monochrome zone pills, concise 1-line Zone header strip (`Zone X · Title` + `done/total` + thin emerald bar), and a clean left vertical timeline rail with crisp 2-line quest cards (Completed check / Active emerald ring / Boss lock or crown).
6. **Floating Pill Bottom Navigation Bar (Mobile):** Inset rounded pill (`#141A24`, `1.dp` `#263042` border, soft elevation) pinned above system navigation bars with 4 destinations: `Trail`, `Checklist`, `Reviews` (with compact count badge), and `Trophies`. On wide desktop/tablet (`>= 840.dp`), a clean top pill switcher provides instant access without obstructing vertical content.
