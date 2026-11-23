# DroidQuest — 100-Day Senior Android Interview Roadmap (KMP + Compose Multiplatform)

A Kotlin Multiplatform + Compose Multiplatform gamified adventure app targeting **Android**, **iOS**, **Desktop JVM**, and **Web (Kotlin/Wasm)**, built with the Kotlin Toolchain (`0.14.0`).

## Architecture & Highlights
- **Curriculum (1:1 Port):** 5 illustrated Adventure Zones, 33 Quests (including 5 Boss Battles), 86 Subtopics, curated external Study Links, and Senior Interview Active-Recall Q&A cards.
- **Spaced Repetition Engine:** `1d → 3d → 7d → 21d → 60d` review ladder (`Forgot`, `Hard`, `Good`, `Easy`), 100-day sprint countdown, daily pace calculator, and daily study streak tracker.
- **Offline-First Persistence:**
  - **Android, iOS & Desktop JVM:** Room KMP (`2.8.5`) + `BundledSQLiteDriver` (`2.7.1`) in `shared/src@nonWeb/`.
  - **Web (Kotlin/Wasm):** Browser `localStorage` fallback in `shared/src@wasmJs/`.
  - **100% Web Parity:** Export/Import `progress.json` using the exact same JSON schema as DroidQuest Web.
- **Material 3 Design System (`android-compose-design`):** 5-step Midnight Observatory tonal surface scale (`#0B1220` base), bundled `Fredoka` + `Nunito` + `JetBrains Mono` typography, purposeful varied corner radii, Lucide-style custom Compose icons, tactile 3D ledge buttons, circular progress rings, and celebration confetti.
- **DI & State Management:** Koin `4.2.2` + UDF `DroidQuestViewModel` (`StateFlow` + intents + haptic effects).
- **Daily Review Notifications:** Optional local daily review reminders on Android (`AlarmManager` + `NotificationManager`) and iOS (`UNUserNotificationCenter`).

## Running
- **Android:** `./kotlin run -m androidApp`
- **Desktop JVM:** `./kotlin run -m desktopApp`
- **Web (Wasm):** `./kotlin run -m webApp`
- **iOS:** `./kotlin run -m iosApp` *(run `sudo xcodebuild -runFirstLaunch` once if Xcode hasn't completed first-launch setup)*

## Testing
- **JVM + Room KMP SQLite tests:** `./kotlin test -m shared -p jvm`
- **Android tests:** `./kotlin test -m shared -p android`
- **WebAssembly tests:** `./kotlin test -m shared -p wasmJs`
- **All platforms:** `./kotlin test`
