# DroidQuest — 100-Day Senior Android Interview Roadmap (KMP + Compose Multiplatform)

A mobile-first **Kotlin Multiplatform + Compose Multiplatform** study companion targeting **Android**, **iOS**, and **Web (Kotlin/Wasm)**, built with the Kotlin Toolchain (`0.14.0` / Amper).

## Architecture & Highlights
- **Curriculum:** 5 Adventure Zones, 33 Quests (including 5 Boss Battle mock challenges), 86 Subtopics, curated external Study Links, custom user links, quest notes, and Senior Interview Active-Recall Q&A cards.
- **Spaced Repetition Engine:** `1d → 3d → 7d → 21d → 60d` review ladder (`Forgot`, `Hard`, `Good`, `Easy`), 100-day sprint countdown, daily pace calculator, and daily study streak tracker.
- **Offline-First Persistence:**
  - **Android & iOS (plus `shared [jvm]` test target):** Room KMP (`2.8.5`) + `BundledSQLiteDriver` (`2.7.1`) in `shared/src@nonWeb/`.
  - **Web (Kotlin/Wasm):** Browser `localStorage` fallback in `shared/src@wasmJs/`.
  - **100% Web Parity:** Export/Import `progress.json` using the exact same schema across mobile and web.
- **Minimal Monochrome Design System ([DESIGN.md](DESIGN.md)):** Dark (`#0B0E14`) and Light (`#F8FAFC`) themes with a single disciplined Emerald accent (`#22C55E` / `#16A34A`), bundled `Nunito` + `JetBrains Mono` typography, tokenized spacing/sizing/motion, Revolut-style compacting floating bottom navigation bar, and smooth screen/sheet transitions.
- **DI & State Management:** Koin `4.2.2` + UDF `DroidQuestViewModel` (`StateFlow` + intents + haptic effects).
- **Daily Review Notifications:** Optional local daily review reminders on Android (`AlarmManager` + `NotificationManager`) and iOS (`UNUserNotificationCenter`).
- **Security & CI:** Gitleaks pre-commit hook (`.githooks/pre-commit`) + GitHub Actions CI & secret scanning.

## Running
- **Android:** `./kotlin run -m androidApp`
- **iOS:** `./kotlin run -m iosApp` *(run `sudo xcodebuild -runFirstLaunch` once if Xcode hasn't completed first-launch setup)*
- **Web (Wasm):** `./kotlin run -m webApp`

## Testing
- **Fast JVM + Room KMP SQLite tests:** `./kotlin test -m shared -p jvm`
- **Android tests:** `./kotlin test -m shared -p android`
- **WebAssembly tests:** `./kotlin test -m shared -p wasmJs`
- **All platforms:** `./kotlin test`

## Security Hooks
Enable the repository's versioned Gitleaks pre-commit hook after cloning:
```bash
git config core.hooksPath .githooks
```
