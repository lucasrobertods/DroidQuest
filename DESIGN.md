# DroidQuest — Design System Specification (`DESIGN.md`)

Exported and refined from Google Stitch project **DroidQuest Mobile Redesign** (`projects/5313316087187846071`, core screens `63f6a10c0d1a4e28a3363c987522f021` — *Minimal Monochrome Refinement* and `21a75fad759940bcb5317510273433c0` — *Minimal Monochrome Light Mode*; Settings, Language & Study Schedule screens `49f4551fac73488cb8a72d60893daf1e` / `bef3d9fb7f7349afa52149a824a2234b` Dark and `7cca606b5e31452ead4a1438645fe0ab` / `e6abd437c9e349b3a8d51603cd4472fd` Light).

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

## 4. Tokenized Spacing, Sizing, Borders & Motion (`Tokens.kt`)

All layout dimensions, border widths, and animation timings are exposed through `DroidQuestTheme`:
- **`DroidQuestTheme.spacing`**: `xxs (2dp)`, `xs (4dp)`, `sm (6dp)`, `md (8dp)`, `base (10dp)`, `lg (12dp)`, `xl (14dp)`, `xxl (16dp)`, `section (18dp)`, `sheetSection (22dp)`, `screenHorizontal (16dp)`, `cardPadding (16dp)`, `cardPaddingCompact (14dp)`, `cardPaddingSpacious (18dp)`, `dialogPadding (20dp)`.
- **`DroidQuestTheme.sizing`**: `maxContentWidth (1040dp)`, `wideBreakpoint (820dp)`, `mediumBreakpoint (560dp)`, `iconXs..Xl (12–20dp)`, `progressBarThin/Medium/Standard (4/5/6dp)`, `navPillMaxWidth (460dp)`, `elevationNavPill (14dp)`, `elevationModal (24dp)`.
- **`DroidQuestTheme.borders`**: `hairline (1dp)`, `medium (1.5dp)`, `emphasis (2dp)`.
- **`DroidQuestTheme.motion`**: `pressMs (80ms)`, `fastMs (180ms)`, `standardMs (240ms)`, `sheetMs (280ms)`, `progressMs (450ms)`, `celebrationMs (1150ms)`.
- **Corner Radii (`Shape.kt`)**: `MicroBadge (8dp)`, `TactileButton / HudPill (12dp)`, `QuestCard (16dp)`, `ZoneBanner (20dp)`, `Dialog (22dp)`, `SheetTop (24dp top)`, `FullPill (CircleShape)`.

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

---

## 6. Settings & Preferences Component Specifications (`SettingsDialog`)

Exported from Stitch screens **DroidQuest — Settings, Language & Study Schedule** (Dark Mode: `49f4551fac73488cb8a72d60893daf1e` / `bef3d9fb7f7349afa52149a824a2234b`; Light Mode: `7cca606b5e31452ead4a1438645fe0ab` / `e6abd437c9e349b3a8d51603cd4472fd`).

### 6.1 Dialog Shell & Header Chrome
- **Modal Container:** Centered surface (`max-width = sizing.dialogMaxWidth (520dp)`, `max-height = sizing.dialogMaxHeight (600dp)` scrollable content body), `DroidQuestCornerShapes.Dialog (22dp)`, `1dp` `outline` border (`#2B3548` dark / `#CBD5E1` light), `sizing.elevationModal (24dp)` shadow elevation, `spacing.dialogPadding (20dp)` content padding, and `spacing.section (18dp)` vertical spacing between sections.
- **Header Row:**
  - Left: `28dp` (`sizing.appBadgeSize`) `MicroBadge (8dp)` Settings icon emblem (`primaryContainer` fill `#0E2419` dark / `#ECFDF5` light, `1dp` `primary.copy(alpha = 0.35f)` border, `16dp` `primary` gear icon) + `Settings & Backup` (`headlineSmall`, `onSurface`).
  - Right: `34dp` (`sizing.ghostButtonSize`) circular close button (`surfaceVariant` fill, `1dp` `outlineVariant` border, `16dp` `Close` icon in `onSurfaceVariant`).
  - Bottom `1dp` `HorizontalDivider` (`outlineVariant`).

### 6.2 Segmented Preference Selectors (`Appearance` & `Language`)
Both `Appearance` and `Language` share a unified segmented selector pattern so theme and locale switching feel cohesive across Dark and Light Minimal Monochrome styles:

| Property | `AppearanceSection` (`System / Light / Dark`) | `LanguageSection` (`System / EN / PT-BR / ES`) |
| :--- | :--- | :--- |
| **Eyebrow Label** | `APPEARANCE` (`labelSmall`, `onSurfaceVariant`) + optional `THEME MODE` right mono metadata | `LANGUAGE` (`labelSmall`, `onSurfaceVariant`) + optional `APP LOCALE` right mono metadata |
| **Layout Grid** | 3 equal-width (`weight(1f)`) `TactileButton (12dp)` segments, `spacing.md (8dp)` gap | 4 equal-width (`weight(1f)`) `TactileButton (12dp)` segments, `spacing.sm–md (6dp–8dp)` gap |
| **Segment Padding** | `spacing.base (10dp)` vertical centered box | `spacing.base (10dp)` vertical centered box |
| **Typography** | `labelLarge` (`Nunito SemiBold/Bold`) | `labelLarge` for `System`; `labelLarge` / `JetBrains Mono` (`mono.badge`) for `EN`, `PT-BR`, `ES` |
| **Selected State (Dark)** | `surfaceContainerHigh` (`#181F2C`) fill, `1dp` `primary.copy(alpha = 0.65f)` (`#22C55E`) border, `primary` (`#22C55E`) text | `surfaceContainerHigh` (`#181F2C`) fill, `1dp` `primary.copy(alpha = 0.65f)` (`#22C55E`) border, `primary` (`#22C55E`) text |
| **Selected State (Light)** | `PaperContainer` (`#FFFFFF`) fill, `1dp` `primary.copy(alpha = 0.65f)` (`#16A34A`) or `#CBD5E1` border, `primary` (`#16A34A`) text | `PaperContainer` (`#FFFFFF`) fill, `1dp` `primary.copy(alpha = 0.65f)` (`#16A34A`) or `#CBD5E1` border, `primary` (`#16A34A`) text |
| **Unselected State (Dark)** | `surfaceContainer` (`#131822`) / `surfaceContainerLowest` (`#0E131D`) fill, `1dp` `outlineVariant` (`#1E2634`) border, `onSurfaceVariant` (`#8A94A6`) text | `surfaceContainer` (`#131822`) / `surfaceContainerLowest` (`#0E131D`) fill, `1dp` `outlineVariant` (`#1E2634`) border, `onSurfaceVariant` (`#8A94A6`) text |
| **Unselected State (Light)** | `PaperLowest` (`#F1F5F9`) fill, `1dp` `outlineVariant` (`#E2E8F0`) border, `onSurfaceVariant` (`#64748B`) text | `PaperLowest` (`#F1F5F9`) fill, `1dp` `outlineVariant` (`#E2E8F0`) border, `onSurfaceVariant` (`#64748B`) text |

### 6.3 Study Schedule Reminder Card (`StudyScheduleCard`)
Located under the `NOTIFICATIONS` section (which displays a live `• ACTIVE` emerald indicator in `mono.micro` whenever Study Schedule or Daily Review notifications are enabled):

1. **Card Container:**
   - Shape: `DroidQuestCornerShapes.QuestCard (16dp)`, inner padding `spacing.cardPaddingCompact (14dp)`, vertical stack gap `spacing.lg (12dp)`.
   - Surface: `surfaceContainer` (`#131822` dark / `#FFFFFF` light).
   - Border: `1dp` `primary.copy(alpha = 0.35f)` when `studyReminderEnabled == true`; `1dp` `outlineVariant` (`#1E2634` dark / `#E2E8F0` light) when disabled.
2. **Card Header & Toggle (`StudyScheduleHeaderRow`):**
   - Left `28dp` `MicroBadge (8dp)` `CalendarClock` icon badge (`active = enabled`).
   - Title: `Study Schedule` (`titleSmall`, `onSurface`).
   - Dynamic 1-line subtitle (`bodySmall`, `onSurfaceVariant`):
     - Enabled with days: `<DaysSummary> at <HH:MM> · Opens Next Quest` (e.g., `Mon–Fri at 19:00 · Opens Next Quest`).
     - Paused with days: `Paused · <DaysSummary> at <HH:MM>`.
     - Empty days: `Choose weekdays and study time`.
   - Right: Emerald `Switch` (`checkedTrackColor = primary`, `checkedThumbColor = onPrimary`, `uncheckedTrackColor = surfaceContainerHigh`, `uncheckedBorderColor = outline`).
3. **Monochrome Weekday Pills (`M T W T F S S` — `StudyScheduleWeekdaysSection`):**
   - **Subheader Row:** Left `TARGET DAYS` (`mono.micro`, `onSurfaceVariant`), right `<N> DAY(S) / WK` (`mono.micro`, `primary` when enabled and non-empty, otherwise `onSurfaceVariant`).
   - **7-Column Pill Row:** 7 equal-width (`weight(1f)`) pills (`height = sizing.ghostButtonSize (34dp)`, `MicroBadge (8dp)` radius, `spacing.sm (6dp)` gap, centered `JetBrains Mono` `mono.badge` initial `M`, `T`, `W`, `T`, `F`, `S`, `S`, accessibility `contentDescription = "<Day> study reminder"`).
   - **3 Visual States (Dark / Light):**
     | State | Fill Token (Dark / Light) | Border Token | Text Token (Dark / Light) |
     | :--- | :--- | :--- | :--- |
     | **Selected & Enabled** | `primary` (`#22C55E` / `#16A34A`) | `1dp` `primary` | `onPrimary` (`#051F10` / `#FFFFFF`) |
     | **Selected & Paused** | `primaryContainer` (`#0E2419` / `#ECFDF5`) | `1dp` `primary.copy(alpha = 0.55f)` | `primary` (`#22C55E` / `#16A34A`) |
     | **Unselected** | `surfaceContainerLowest` (`#0E131D` / `#F1F5F9`) | `1dp` `outlineVariant` (`#1E2634` / `#E2E8F0`) | `onSurfaceVariant` (`#8A94A6` / `#64748B`) |
4. **Study Time Selector Bar & Dual Steppers (`StudyScheduleTimeBar`):**
   - Recessed bar: `surfaceContainerLowest` (`#0E131D` dark / `#F1F5F9` light), `TactileButton (12dp)` radius, `1dp` `outlineVariant` border, padding `12dp` horizontal × `8dp` vertical.
   - Left label: `14dp` (`sizing.iconSm`) `Clock` icon (`onSurfaceVariant`) + `Study time` (`labelMedium`, `onSurface`).
   - Right stepper cluster (`[−] HH [+] : [−] MM [+]`):
     - Two `TimeStepperGroup` pills separated by `:` (`mono.metric`, `onSurfaceVariant`).
     - Each `TimeStepperGroup` uses `surfaceContainerHigh` (`#181F2C` dark / `#FFFFFF` light), `MicroBadge (8dp)` radius, `1dp` `outlineVariant` border, `4dp` horizontal × `2dp` vertical padding, and `22dp` (`sizing.checkCircleSize`) `−` / `+` buttons flanking a 2-digit tabular value (`mono.metric`, `onSurface`).
     - Hour stepper wraps `00..23` (`±1h`); minute stepper wraps `00..55` in `5-minute` increments (`±5m`).
5. **Quick Time Preset Chips (`08:00 / 12:30 / 19:00 / 21:00` — `StudySchedulePresetsRow`):**
   - Inline `Presets:` label (`mono.micro`, `onSurfaceVariant`) followed by 4 equal-width (`weight(1f)`) `MicroBadge (8dp)` chips (`spacing.sm (6dp)` gap, `6dp` vertical padding, `mono.micro` text).
   - Matching preset (`hour == presetHour && minute == presetMinute`) highlights with `primaryContainer` fill (`#0E2419` dark / `#ECFDF5` light), `1dp` `primary.copy(alpha = 0.55f)` border, and `primary` text; non-matching chips use `surfaceContainerLowest` fill, `1dp` `outlineVariant` border, and `onSurfaceVariant` text.

### 6.4 Daily Review Reminder & Supporting Settings Sections
- **Daily Review Reminder Card (`DailyReminderCard`):** Companion `QuestCard (16dp)` below `StudyScheduleCard` with `Bell` icon badge, `Daily Review Reminder` title, `Spaced repetition alert at HH:00` subtitle, Emerald toggle switch, and 4 equal-width (`weight(1f)`) `MicroBadge (8dp)` hour preset chips (`09:00`, `13:00`, `19:00`, `21:00`) using the same active/inactive token mapping as study presets.
- **100-Day Sprint Start Date (`SprintStartDateSection`):** `100-DAY SPRINT START DATE` eyebrow + row containing a `weight(1f)` `OutlinedTextField` (`YYYY-MM-DD`, `TactileButton (12dp)`) and a solid Emerald `PrimaryActionButton` (`Apply`).
- **Local Persistence Status (`LocalPersistenceCard`):** `QuestCard (16dp)` displaying `HardDrive` emerald badge, storage engine title (`Room SQLite (Offline-First)` + `6dp` emerald status dot), and compatibility subtitle (`saved · progress.json compatible`).
- **Backup & Restore (`BackupRestoreSection`):** 2-column tonal action row (`Export JSON` and `Import JSON` using `SecondaryTonalButton`), secondary inline text links (`Copy JSON · Paste JSON Manually`), and an expandable `PasteJsonImportBox` (`OutlinedTextField` + `Restore JSON` CTA).
- **Danger Zone (`DangerZoneResetSection`):** Quiet `Reset all roadmap progress` destructive action (`RotateCcw` icon + `labelLarge` in `error` `#E55365`) that expands into a 2-step confirmation card (`DangerZoneConfirmCard` on `errorContainer` with `Cancel` and `Reset` buttons).
