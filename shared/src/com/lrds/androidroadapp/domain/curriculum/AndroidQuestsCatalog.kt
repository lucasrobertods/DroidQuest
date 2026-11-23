package com.lrds.androidroadapp.domain.curriculum

import com.lrds.androidroadapp.domain.model.InterviewQuestion
import com.lrds.androidroadapp.domain.model.PillarId
import com.lrds.androidroadapp.domain.model.QuestModule
import com.lrds.androidroadapp.domain.model.ResourceType
import com.lrds.androidroadapp.domain.model.StudyResource
import com.lrds.androidroadapp.domain.model.SubTopic
import com.lrds.androidroadapp.domain.model.ZoneId

object AndroidQuestsCatalog {
    val quests: List<QuestModule> = listOf(
        // =========================================================================
        // ZONE 1: EMERALD FOUNDATIONS & MODERN UI
        // =========================================================================
        QuestModule(
            id = "q1-android-fundamentals",
            zoneId = ZoneId.EMERALD,
            pillar = PillarId.ANDROID,
            order = 1,
            title = "Android Fundamentals & Internals",
            subtitle = "OS architecture, ART runtime, Context, Process Death & Gradle basics",
            dayRange = "Days 1–4",
            estimatedHours = 6,
            sourceBadge = "Module 1",
            icon = "Cpu",
            subtopics = listOf(
                SubTopic(
                    id = "af-1-os-art",
                    title = "Android OS Stack, ART, APK vs AAB & Manifest",
                    description = "Understand Linux kernel, Binder IPC, Zygote process fork, ART (AOT + JIT + Profile-Guided Compilation), APK vs Android App Bundle (AAB), and manifest merging.",
                    xp = 50,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-af-1",
                            title = "Android Platform Architecture (Official Docs)",
                            url = "https://developer.android.com/guide/platform",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-af-2",
                            title = "Android Runtime (ART) & Dalvik Overview",
                            url = "https://source.android.com/docs/core/runtime",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-af-3",
                            title = "YouTube: Android Internals & ART Explained",
                            url = "https://www.youtube.com/results?search_query=Android+Runtime+ART+Zygote+Binder+internals+explained",
                            type = ResourceType.YOUTUBE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "af-2-context-process-death",
                    title = "Context Types, Configuration Changes & Process Death",
                    description = "Master Application vs Activity vs Service Context (preventing memory leaks), SavedInstanceState, ViewModel survival vs OS Process Death restoration.",
                    xp = 60,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-af-4",
                            title = "Saving UI States & Handling Process Death (Official)",
                            url = "https://developer.android.com/topic/libraries/architecture/saving-states",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-af-5",
                            title = "Philipp Lackner: Process Death & Context Deep Dive (YouTube)",
                            url = "https://www.youtube.com/results?search_query=Philipp+Lackner+process+death+Android+Context",
                            type = ResourceType.YOUTUBE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "af-3-gradle-basics",
                    title = "Gradle Build Lifecycle, Variants, Flavors & Version Catalogs",
                    description = "Initialization → Configuration → Execution phases, productFlavors, buildTypes, libs.versions.toml, and KSP vs KAPT.",
                    xp = 50,
                    resources = listOf(
                        StudyResource(
                            id = "r-af-6",
                            title = "Configure Your Build & Variants (Official Docs)",
                            url = "https://developer.android.com/build",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-af-7",
                            title = "Migrate to Gradle Version Catalogs",
                            url = "https://developer.android.com/build/migrate-to-catalogs",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "What is the difference between Configuration Change and Process Death? How do you preserve state across both?",
                    keyPoints = "Config change destroys/recreates Activity in the same process (ViewModel survives). Process death kills the OS process when backgrounded (ViewModel is lost; must use SavedStateHandle, rememberSaveable, or persistent DB/DataStore).",
                ),
                InterviewQuestion(
                    question = "Why does holding an Activity Context in a Singleton or long-lived object cause a memory leak?",
                    keyPoints = "Activity holds references to its entire View hierarchy and Window. A static/Singleton reference prevents GC after Activity.onDestroy(). Use applicationContext for non-UI singletons.",
                ),
                InterviewQuestion(
                    question = "How does ART improve performance compared to legacy Dalvik?",
                    keyPoints = "ART uses hybrid AOT + JIT + Profile-Guided Optimization (and Baseline Profiles), better garbage collection with fewer pauses, and faster cold starts without interpreting every bytecode instruction.",
                ),
            ),
        ),
        QuestModule(
            id = "q2-kotlin-internals",
            zoneId = ZoneId.EMERALD,
            pillar = PillarId.ANDROID,
            order = 2,
            title = "Kotlin Deep Dive & Internals",
            subtitle = "Bytecode-level mastery: inline/reified, variance, delegation & collections",
            dayRange = "Days 5–8",
            estimatedHours = 7,
            highYield = true,
            sourceBadge = "Senior US/EU Upgrade",
            icon = "Code2",
            subtopics = listOf(
                SubTopic(
                    id = "kt-1-inline-reified",
                    title = "Higher-Order Functions: inline, noinline, crossinline & reified",
                    description = "Understand FunctionN object allocation overhead on the JVM, how inline eliminates lambda allocations, non-local returns, crossinline in async callbacks, and reified generics.",
                    xp = 70,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-kt-1",
                            title = "Kotlin Official Docs: Inline Functions",
                            url = "https://kotlinlang.org/docs/inline-functions.html",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-kt-2",
                            title = "YouTube: Kotlin inline, noinline, crossinline Explained",
                            url = "https://www.youtube.com/results?search_query=Kotlin+inline+noinline+crossinline+reified+bytecode",
                            type = ResourceType.YOUTUBE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "kt-2-types-delegation",
                    title = "Sealed Hierarchies, Value Classes, Scope Functions & Delegation",
                    description = "Exhaustive when with sealed interfaces, zero-overhead @JvmInline value classes, let/run/with/apply/also distinctions, and class/property delegation (by lazy, custom delegates).",
                    xp = 60,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-kt-3",
                            title = "Kotlin Docs: Inline Value Classes & Sealed Interfaces",
                            url = "https://kotlinlang.org/docs/inline-classes.html",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-kt-4",
                            title = "Kotlin Docs: Delegated Properties",
                            url = "https://kotlinlang.org/docs/delegated-properties.html",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "kt-3-generics-sequences",
                    title = "Generics Variance (in / out / *) & Sequences vs Collections",
                    description = "Declaration-site vs use-site variance (PECS in Kotlin), type erasure, and lazy Sequence pipelines vs eager Iterable intermediate list allocations.",
                    xp = 65,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-kt-5",
                            title = "Kotlin Docs: Generics & Variance (in, out)",
                            url = "https://kotlinlang.org/docs/generics.html",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-kt-6",
                            title = "Kotlin Docs: Sequences Performance Guide",
                            url = "https://kotlinlang.org/docs/sequences.html",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "When should you NOT use the `inline` keyword on a Kotlin function?",
                    keyPoints = "Avoid inlining large functions without lambda parameters (increases bytecode/APK size and hurts JIT) or when you need to store the lambda parameter as a property/variable.",
                ),
                InterviewQuestion(
                    question = "Explain the difference between `out T` and `in T` in Kotlin generics with an Android example.",
                    keyPoints = "`out T` is covariant (producer only, e.g. `List<out T>`, `Flow<out T>`). `in T` is contravariant (consumer only, e.g. `Comparable<in T>` or an event handler callback).",
                ),
                InterviewQuestion(
                    question = "When is `asSequence()` faster or slower than standard Kotlin `Iterable` chains?",
                    keyPoints = "Sequences avoid allocating intermediate collections on large datasets or multi-step pipelines with short-circuit ops like `first{}` or `take()`. For tiny lists (e.g. <50 items) and single ops, eager `Iterable` has less wrapper overhead.",
                ),
            ),
        ),
        QuestModule(
            id = "q3-activities-navigation",
            zoneId = ZoneId.EMERALD,
            pillar = PillarId.ANDROID,
            order = 3,
            title = "Activities, Intents & Modern Navigation",
            subtitle = "Lifecycle edge cases, Launch Modes, Deep Links & Type-Safe Navigation",
            dayRange = "Days 9–12",
            estimatedHours = 6,
            sourceBadge = "Module 2",
            icon = "Navigation",
            subtopics = listOf(
                SubTopic(
                    id = "an-1-lifecycle-launchmodes",
                    title = "Activity/Fragment Lifecycle & Launch Modes (Back Stack)",
                    description = "Exact lifecycle callbacks during A→B transition, transparent dialogs, multi-window, and launch modes (standard, singleTop, singleTask, singleInstance) + Intent flags.",
                    xp = 60,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-an-1",
                            title = "Tasks and the Back Stack (Official Docs)",
                            url = "https://developer.android.com/guide/components/activities/tasks-and-back-stack",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-an-2",
                            title = "Activity Lifecycle Deep Dive (Official Docs)",
                            url = "https://developer.android.com/guide/components/activities/activity-lifecycle",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "an-2-intents-deeplinks",
                    title = "Explicit/Implicit Intents, App Links & PendingIntent Security",
                    description = "Intent resolution, Digital Asset Links (assetlinks.json) for verified App Links, FLAG_IMMUTABLE on PendingIntents, and handling cold vs warm deep link routing.",
                    xp = 55,
                    resources = listOf(
                        StudyResource(
                            id = "r-an-3",
                            title = "Verify Android App Links (Official Docs)",
                            url = "https://developer.android.com/training/app-links/verify-android-applinks",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-an-4",
                            title = "YouTube: Deep Links & App Links in Android",
                            url = "https://www.youtube.com/results?search_query=Android+Deep+Links+App+Links+Philipp+Lackner",
                            type = ResourceType.YOUTUBE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "an-3-navigation-compose",
                    title = "Single-Activity Architecture, Type-Safe Navigation & Nav3 Awareness",
                    description = "Type-safe Navigation Compose with @Serializable routes, nested graphs, returning results, multiple back stacks (BottomNav), predictive back, and Navigation 3 concepts.",
                    xp = 65,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-an-5",
                            title = "Navigation with Compose & Type Safety (Official)",
                            url = "https://developer.android.com/develop/ui/compose/navigation",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-an-6",
                            title = "Android Developers YouTube: Type-Safe Navigation Compose",
                            url = "https://www.youtube.com/results?search_query=Android+Developers+Type+Safe+Navigation+Compose",
                            type = ResourceType.YOUTUBE,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "When Activity A launches Activity B, in what exact order do their lifecycle callbacks execute?",
                    keyPoints = "A.onPause() → B.onCreate() → B.onStart() → B.onResume() → A.onStop() (and A.onSaveInstanceState() if applicable). Keep A.onPause() lightweight so B renders fast!",
                ),
                InterviewQuestion(
                    question = "What is the difference between `singleTop` and `singleTask` launch modes?",
                    keyPoints = "`singleTop` reuses the instance only if it is ALREADY at the top of the stack (calling `onNewIntent`). `singleTask` clears all activities above the existing instance in the task (`CLEAR_TOP` behavior) and delivers `onNewIntent`.",
                ),
            ),
        ),
        QuestModule(
            id = "q4-jetpack-compose",
            zoneId = ZoneId.EMERALD,
            pillar = PillarId.ANDROID,
            order = 4,
            title = "Jetpack Compose & Modern UI Architecture",
            subtitle = "Composition phases, Side Effects, Stability, Recomposition & Custom Layouts",
            dayRange = "Days 13–18",
            estimatedHours = 10,
            highYield = true,
            sourceBadge = "Module 3 • High Priority",
            icon = "Sparkles",
            subtopics = listOf(
                SubTopic(
                    id = "jc-1-mental-model-state",
                    title = "Three Phases, State Hoisting, remember & rememberSaveable",
                    description = "Composition → Layout → Drawing phases; Slot Table mental model; unidirectional state hoisting; custom Savers; and deferring state reads with lambda modifiers (`offset { }`, `drawBehind { }`).",
                    xp = 75,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-jc-1",
                            title = "Thinking in Compose & Lifecycle (Official Docs)",
                            url = "https://developer.android.com/develop/ui/compose/mental-model",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-jc-2",
                            title = "Jetpack Compose Phases (Official Docs)",
                            url = "https://developer.android.com/develop/ui/compose/phases",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "jc-2-side-effects",
                    title = "Side Effects Mastery (LaunchedEffect, DisposableEffect, derivedStateOf)",
                    description = "Choosing the right effect API, `rememberUpdatedState` for stale lambda captures, `snapshotFlow`, `produceState`, and avoiding recomposition loops.",
                    xp = 75,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-jc-3",
                            title = "Side-effects in Compose (Official Docs)",
                            url = "https://developer.android.com/develop/ui/compose/side-effects",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-jc-4",
                            title = "YouTube: Jetpack Compose Side Effects Deep Dive",
                            url = "https://www.youtube.com/results?search_query=Jetpack+Compose+Side+Effects+LaunchedEffect+derivedStateOf+Android+Developers",
                            type = ResourceType.YOUTUBE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "jc-3-recomposition-stability",
                    title = "Stability, Strong Skipping Mode & LazyColumn Performance",
                    description = "Diagnosing unnecessary recompositions using Compose Compiler Metrics & Layout Inspector, `@Immutable` vs `@Stable`, ImmutableList, `key`/`contentType` in Lazy lists, and Strong Skipping.",
                    xp = 80,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-jc-5",
                            title = "Compose Performance Best Practices (Official)",
                            url = "https://developer.android.com/develop/ui/compose/performance/bestpractices",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-jc-6",
                            title = "Stability in Compose & Strong Skipping Mode",
                            url = "https://developer.android.com/develop/ui/compose/performance/stability",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "jc-4-custom-layouts-adaptive",
                    title = "Custom Layouts, Modifiers Order, Animations & Adaptive UI",
                    description = "Modifier chaining order (padding before vs after clickable/background), `Layout` composable, `SubcomposeLayout`, `Modifier.Node`, Canvas animations, and WindowSizeClass for tablets/foldables.",
                    xp = 65,
                    resources = listOf(
                        StudyResource(
                            id = "r-jc-7",
                            title = "Custom Layouts in Compose (Official)",
                            url = "https://developer.android.com/develop/ui/compose/layouts/custom",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-jc-8",
                            title = "Build Adaptive Apps with Compose",
                            url = "https://developer.android.com/develop/ui/compose/layouts/adaptive",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "How do you prevent a scroll offset animation from recomposing the entire screen on every frame in Compose?",
                    keyPoints = "Defer state reads to the Layout or Drawing phase using lambda-based modifiers like `Modifier.offset { IntOffset(...) }` or `Modifier.graphicsLayer { translationY = ... }` instead of reading the state directly in Composition.",
                ),
                InterviewQuestion(
                    question = "When should you use `derivedStateOf` vs `remember(key)`?",
                    keyPoints = "Use `derivedStateOf` when an input state changes MORE frequently than the derived boolean/output (e.g. `lazyListState.firstVisibleItemIndex > 0` to show a scroll-to-top button). Use `remember(a, b)` when inputs and outputs change at the same rate.",
                ),
                InterviewQuestion(
                    question = "Why are standard Kotlin `List<T>` parameters considered unstable by the Compose compiler, and how does Strong Skipping Mode help?",
                    keyPoints = "`List` is an interface whose backing implementation could be `MutableList`. Historically this prevented skipping unless wrapped or using `kotlinx.collections.immutable`. Strong Skipping Mode allows skipping via instance equality (`===`) for unstable params and auto-remembers lambdas.",
                ),
            ),
        ),
        QuestModule(
            id = "q5-boss-1",
            zoneId = ZoneId.EMERALD,
            pillar = PillarId.ANDROID,
            order = 5,
            title = "BOSS BATTLE 1: Core Android & Compose Live Coding",
            subtitle = "Defeat the Emerald Guardian with a timed live-coding & oral interview drill",
            dayRange = "Days 19–20",
            estimatedHours = 3,
            isBoss = true,
            highYield = true,
            sourceBadge = "Zone 1 Boss Battle",
            icon = "Swords",
            subtopics = listOf(
                SubTopic(
                    id = "b1-1-live-coding",
                    title = "Timed 45-Min Challenge: Searchable Filterable List in Compose",
                    description = "Build a Compose screen with debounced search, Pull-to-Refresh, error/empty states, process-death survival (`SavedStateHandle`), and zero unnecessary recompositions.",
                    xp = 150,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-b1-1",
                            title = "Official Compose Samples Repository (GitHub)",
                            url = "https://github.com/android/compose-samples",
                            type = ResourceType.REPO,
                        ),
                    ),
                ),
                SubTopic(
                    id = "b1-2-oral-drill",
                    title = "English Oral Defense: Explain 10 Core Concepts Out Loud",
                    description = "Record yourself for 10 minutes explaining ART, Process Death, inline/reified, Activity Launch Modes, Compose 3 Phases, and Side Effects clearly in English.",
                    xp = 100,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-b1-2",
                            title = "Android Interview Questions Guide (GitHub)",
                            url = "https://github.com/amitshekhariitbhu/android-interview-questions",
                            type = ResourceType.REPO,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "Boss Prompt: Walk me through how you would debug a janky LazyColumn in Jetpack Compose from start to finish.",
                    keyPoints = "1) Test in Release build with R8 + Baseline Profile (not debug!). 2) Add stable `key` and `contentType`. 3) Inspect recomposition counts in Layout Inspector / Compose Compiler Metrics. 4) Capture a Perfetto system trace to verify frame times & main thread work.",
                ),
            ),
        ),

        // =========================================================================
        // ZONE 2: SAPPHIRE CITADEL OF ARCHITECTURE & DATA
        // =========================================================================
        QuestModule(
            id = "q6-coroutines-flow",
            zoneId = ZoneId.SAPPHIRE,
            pillar = PillarId.ANDROID,
            order = 6,
            title = "Threads, Kotlin Coroutines & Flow",
            subtitle = "Structured concurrency, Dispatchers, Exception propagation & Reactive Flows",
            dayRange = "Days 21–26",
            estimatedHours = 9,
            highYield = true,
            sourceBadge = "Module 5 • High Priority",
            icon = "Zap",
            subtopics = listOf(
                SubTopic(
                    id = "cf-1-threads-looper",
                    title = "JVM Threads, Looper, Handler, MessageQueue & ANRs",
                    description = "How Android Main Thread event loop works (`Looper.loop()`), `Handler` message posting, thread synchronization (`volatile`, `Atomic*`, `Mutex`), and ANR thresholds (5s input, 10s broadcast).",
                    xp = 60,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-cf-1",
                            title = "Processes and Threads Overview (Official Docs)",
                            url = "https://developer.android.com/guide/components/processes-and-threads",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-cf-2",
                            title = "Diagnose & Fix ANRs (Official Docs)",
                            url = "https://developer.android.com/topic/performance/vitals/anr",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "cf-2-structured-concurrency",
                    title = "Suspend Internals (CPS), Scopes, Job vs SupervisorJob & Cancellation",
                    description = "How `suspend` compiles to state machines (`Continuation`), `Dispatchers.Main`/`IO`/`Default`/`Main.immediate`, cooperative cancellation (`ensureActive`, `CancellationException` gotcha!), and `supervisorScope`.",
                    xp = 80,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-cf-3",
                            title = "Kotlin Coroutines Best Practices on Android (Official)",
                            url = "https://developer.android.com/kotlin/coroutines/coroutines-best-practices",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-cf-4",
                            title = "YouTube: Roman Elizarov / Philipp Lackner Coroutines Internals",
                            url = "https://www.youtube.com/results?search_query=Kotlin+Coroutines+structured+concurrency+SupervisorJob+exception+handling",
                            type = ResourceType.YOUTUBE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "cf-3-flow-stateflow",
                    title = "Cold Flow vs StateFlow vs SharedFlow & Operators",
                    description = "Cold vs Hot streams, `stateIn(SharingStarted.WhileSubscribed(5000))`, `collectAsStateWithLifecycle()`, `flatMapLatest` vs `flatMapConcat` vs `flatMapMerge`, `combine`, `buffer`, `conflate`, and `Channel` for one-off events.",
                    xp = 80,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-cf-5",
                            title = "Kotlin Flows on Android (Official Docs)",
                            url = "https://developer.android.com/kotlin/flow",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-cf-6",
                            title = "StateFlow and SharedFlow Guide (Official)",
                            url = "https://developer.android.com/kotlin/flow/stateflow-and-sharedflow",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "Why should you NEVER swallow `CancellationException` inside a generic `catch (e: Exception)` block in a coroutine?",
                    keyPoints = "Structured concurrency relies on `CancellationException` propagating upward to signal cancellation. Swallowing it keeps the coroutine running when its parent/ViewModel is already cancelled, causing leaks and zombie work.",
                ),
                InterviewQuestion(
                    question = "Explain `SharingStarted.WhileSubscribed(5000)` when converting a cold Flow to a StateFlow in a ViewModel.",
                    keyPoints = "It stops upstream collection 5 seconds after the last UI collector unsubscribes (saving battery/network when backgrounded) while surviving quick configuration changes like screen rotation without restarting the upstream flow.",
                ),
                InterviewQuestion(
                    question = "What happens when a child coroutine throws an exception inside `coroutineScope` vs `supervisorScope`?",
                    keyPoints = "In `coroutineScope`, a failing child cancels the parent and ALL sibling coroutines. In `supervisorScope`, a child failure does not cancel siblings or the parent (each child handles its own failure independently).",
                ),
            ),
        ),
        QuestModule(
            id = "q7-architecture-modularization",
            zoneId = ZoneId.SAPPHIRE,
            pillar = PillarId.ANDROID,
            order = 7,
            title = "Architecture at Scale: MVVM, MVI & Modularization",
            subtitle = "UDF, ViewModel internals, Clean Architecture pragmatism & Multi-Module Gradle",
            dayRange = "Days 27–31",
            estimatedHours = 8,
            highYield = true,
            sourceBadge = "Module 6 + Senior Upgrade",
            icon = "Layers",
            subtopics = listOf(
                SubTopic(
                    id = "arch-1-mvvm-mvi-udf",
                    title = "Unidirectional Data Flow (UDF), MVVM vs MVI & ViewModel Internals",
                    description = "How `ViewModelStore` survives config changes, single UiState vs multiple streams, handling one-off UI events (State vs Channel debate), and when MVI shines over MVVM.",
                    xp = 75,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-arch-1",
                            title = "Guide to App Architecture (Official Android)",
                            url = "https://developer.android.com/topic/architecture",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-arch-2",
                            title = "UI Layer & UI Events Architecture (Official)",
                            url = "https://developer.android.com/topic/architecture/ui-layer/events",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "arch-2-clean-arch",
                    title = "Pragmatic Clean Architecture & Repository Pattern",
                    description = "Separating UI, Domain (optional UseCases for reusable/complex business rules), and Data layers without over-engineering; mapping DTO → Entity → Domain → UiModel.",
                    xp = 65,
                    resources = listOf(
                        StudyResource(
                            id = "r-arch-3",
                            title = "Now in Android Official Reference App (GitHub)",
                            url = "https://github.com/android/nowinandroid",
                            type = ResourceType.REPO,
                        ),
                    ),
                ),
                SubTopic(
                    id = "arch-3-modularization",
                    title = "Multi-Module Architecture & Gradle Convention Plugins",
                    description = "Feature vs Core (`:core:network`, `:core:database`, `:core:ui`, `:feature:feed`), `api` vs `implementation` encapsulation, avoiding circular dependencies, build-logic Convention Plugins, and faster incremental builds.",
                    xp = 80,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-arch-4",
                            title = "Guide to Android App Modularization (Official)",
                            url = "https://developer.android.com/topic/modularization",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-arch-5",
                            title = "Now in Android Modularization Learning Journey",
                            url = "https://github.com/android/nowinandroid/blob/main/docs/ModularizationLearningJourney.md",
                            type = ResourceType.ARTICLE,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "How do you handle one-time UI events (like Navigation or Snackbar) cleanly without losing events or re-triggering on rotation?",
                    keyPoints = "Google recommends modeling them as state (e.g., `snackbarMessage: String?` cleared via an `onMessageShown()` callback) so state is strictly observable and survives config changes; `Channel(BUFFERED)` with `Dispatchers.Main.immediate` is a common alternative—be ready to compare trade-offs!",
                ),
                InterviewQuestion(
                    question = "Why does using `implementation` instead of `api` in multi-module Gradle projects dramatically improve build times?",
                    keyPoints = "`implementation` hides transitive dependencies from consumers, so changing an internal module only recompiles direct dependents rather than triggering a cascading ABI recompilation across the whole module graph.",
                ),
            ),
        ),
        QuestModule(
            id = "q8-dependency-injection",
            zoneId = ZoneId.SAPPHIRE,
            pillar = PillarId.ANDROID,
            order = 8,
            title = "Dependency Injection (Hilt, Dagger 2 & Koin)",
            subtitle = "Compile-time graph generation, Scopes, Qualifiers & Multi-Module DI",
            dayRange = "Days 32–34",
            estimatedHours = 5,
            sourceBadge = "Module 7",
            icon = "Plug",
            subtopics = listOf(
                SubTopic(
                    id = "di-1-hilt-core",
                    title = "Hilt Setup, Component Hierarchy, Scopes & @Binds vs @Provides",
                    description = "Understand `SingletonComponent`, `ActivityRetainedComponent`, `ViewModelComponent`, `@Binds` (zero factory overhead for interfaces) vs `@Provides`, `@Qualifier`, and Multibindings (`@IntoSet`/`@IntoMap`).",
                    xp = 65,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-di-1",
                            title = "Dependency Injection with Hilt (Official Docs)",
                            url = "https://developer.android.com/training/dependency-injection/hilt-android",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-di-2",
                            title = "Hilt Official Documentation & Component Lifetimes",
                            url = "https://dagger.dev/hilt/components.html",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "di-2-dagger-koin-comparison",
                    title = "Dagger Internals, Hilt in Multi-Module Apps & Koin Comparison",
                    description = "Compile-time verification (Dagger/Hilt/Anvil/Metro) vs runtime Service Locator (Koin), `@EntryPoint` for non-Hilt classes, and DI strategies for KMP.",
                    xp = 60,
                    resources = listOf(
                        StudyResource(
                            id = "r-di-3",
                            title = "Hilt in Multi-Module Apps (Official)",
                            url = "https://developer.android.com/training/dependency-injection/hilt-multi-module",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-di-4",
                            title = "YouTube: Hilt vs Koin vs Manual DI for Senior Android",
                            url = "https://www.youtube.com/results?search_query=Hilt+vs+Koin+Android+dependency+injection+comparison",
                            type = ResourceType.YOUTUBE,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "Why should you prefer `@Binds` over `@Provides` when binding an interface to an implementation in Hilt/Dagger?",
                    keyPoints = "`@Binds` is an abstract method that generates less boilerplate code and avoids creating an extra Module instance or static factory class at compile time.",
                ),
                InterviewQuestion(
                    question = "What are the trade-offs between Hilt/Dagger and Koin in a large team codebase?",
                    keyPoints = "Hilt/Dagger verifies the entire dependency graph at compile time (catching missing bindings before runtime) at the cost of build-time codegen (KSP helps). Koin has faster builds and works in KMP, but missing dependencies fail at runtime unless covered by `checkModules()` tests.",
                ),
            ),
        ),
        QuestModule(
            id = "q9-local-storage-offline",
            zoneId = ZoneId.SAPPHIRE,
            pillar = PillarId.ANDROID,
            order = 9,
            title = "Local Data Storage, Caching & Offline-First",
            subtitle = "Room internals, Migrations, DataStore, Single Source of Truth & Sync",
            dayRange = "Days 35–38",
            estimatedHours = 7,
            highYield = true,
            sourceBadge = "Module 10",
            icon = "Database",
            subtopics = listOf(
                SubTopic(
                    id = "db-1-room-migrations",
                    title = "Room Database: Schema Design, Indices, Transactions & Migrations",
                    description = "Entities, `@Relation` vs `@Embedded`, SQLite Indices for query speed, `@Transaction`, returning `Flow<T>` from DAOs, AutoMigration vs Manual `Migration`, and testing migrations.",
                    xp = 70,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-db-1",
                            title = "Save Data in a Local DB using Room (Official)",
                            url = "https://developer.android.com/training/data-storage/room",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-db-2",
                            title = "Migrate Your Room Database Safely (Official)",
                            url = "https://developer.android.com/training/data-storage/room/migrating-db-versions",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "db-2-datastore",
                    title = "Preferences DataStore vs Proto DataStore",
                    description = "Why SharedPreferences causes ANRs (`apply()` fsync on UI lifecycle) and lacks error handling; transactional async DataStore with Coroutines/Flow and type-safe Protobuf schemas.",
                    xp = 55,
                    resources = listOf(
                        StudyResource(
                            id = "r-db-3",
                            title = "Jetpack DataStore Guide (Official Docs)",
                            url = "https://developer.android.com/topic/libraries/architecture/datastore",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "db-3-offline-first",
                    title = "Offline-First Architecture, Caching Policies & Conflict Resolution",
                    description = "Single Source of Truth (Room feeds UI, Network updates Room), TTL / ETag / Cache-Control, Optimistic UI updates, mutation queues with WorkManager, and Last-Write-Wins vs CRDT/versioning.",
                    xp = 75,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-db-4",
                            title = "Build an Offline-First App (Official Architecture Guide)",
                            url = "https://developer.android.com/topic/architecture/data-layer/offline-first",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "Why did Google replace SharedPreferences with Jetpack DataStore?",
                    keyPoints = "SharedPreferences performs synchronous disk I/O that can block the UI thread (causing ANRs during `onStop` waiting for `apply()`), has no type safety, no transactional atomicity, and throws unchecked runtime exceptions.",
                ),
                InterviewQuestion(
                    question = "How do you design an optimistic \"Like\" button in an offline-first Android app?",
                    keyPoints = "Immediately update local Room DB (UI observes Room Flow so update is instant), enqueue a persistent sync job (WorkManager) with an idempotency key, and roll back or reconcile with server response if permanent failure occurs.",
                ),
            ),
        ),
        QuestModule(
            id = "q10-networking-pagination",
            zoneId = ZoneId.SAPPHIRE,
            pillar = PillarId.ANDROID,
            order = 10,
            title = "Networking, API Integration & Pagination",
            subtitle = "OkHttp/Retrofit internals, Token Refresh, Serialization & Paging 3",
            dayRange = "Days 39–42",
            estimatedHours = 7,
            highYield = true,
            sourceBadge = "Module 11",
            icon = "Globe",
            subtopics = listOf(
                SubTopic(
                    id = "net-1-okhttp-retrofit",
                    title = "OkHttp Interceptors, Token Refresh (Authenticator) & Resilience",
                    description = "Application vs Network interceptors, thread-safe JWT refresh using `OkHttp Authenticator` + `Mutex` (avoiding refresh storms!), connection pooling, HTTP/2, timeouts, and exponential backoff.",
                    xp = 75,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-net-1",
                            title = "OkHttp Interceptors & Recipes (Official)",
                            url = "https://square.github.io/okhttp/features/interceptors/",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-net-2",
                            title = "YouTube: JWT Token Refresh with OkHttp Authenticator",
                            url = "https://www.youtube.com/results?search_query=Android+OkHttp+Authenticator+JWT+token+refresh+concurrency",
                            type = ResourceType.YOUTUBE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "net-2-serialization-protocols",
                    title = "Kotlinx Serialization, Error Modeling & REST vs GraphQL vs gRPC",
                    description = "Compile-time Kotlinx Serialization (null-safety + default values) vs Moshi/Gson, modeling `NetworkResult` sealed types, and trade-offs between REST, GraphQL (Apollo), and gRPC.",
                    xp = 60,
                    resources = listOf(
                        StudyResource(
                            id = "r-net-3",
                            title = "Kotlinx Serialization Guide",
                            url = "https://kotlinlang.org/docs/serialization.html",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "net-3-pagination",
                    title = "Paging 3 (PagingSource & RemoteMediator) vs Custom Cursor Pagination",
                    description = "Offset vs Cursor/Keyset pagination (why offset breaks on real-time feeds!), Paging 3 `RemoteMediator` + Room SSOT, `collectAsLazyPagingItems()`, and load state footers.",
                    xp = 70,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-net-4",
                            title = "Paging 3 Library Overview (Official Docs)",
                            url = "https://developer.android.com/topic/libraries/architecture/paging/v3-overview",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "If 5 parallel API calls fail with HTTP 401 simultaneously, how do you ensure the auth token is refreshed only ONCE?",
                    keyPoints = "Use an OkHttp `Authenticator` synchronized with a lock/mutex. Inside the lock, check if the stored access token has already been updated compared to the failed request’s Authorization header; if so, retry directly with the new token without calling `/refresh` again.",
                ),
                InterviewQuestion(
                    question = "Why is Cursor-based pagination preferred over Offset/Page-number pagination for social feeds?",
                    keyPoints = "In dynamic feeds where new items are constantly inserted, Offset pagination causes duplicate or skipped items between page fetches and degrades SQL performance (`OFFSET 10000`) on the backend.",
                ),
            ),
        ),
        QuestModule(
            id = "q11-background-services-workmanager",
            zoneId = ZoneId.SAPPHIRE,
            pillar = PillarId.ANDROID,
            order = 11,
            title = "Services, Receivers, Notifications & WorkManager",
            subtitle = "Foreground services, Android 14+ restrictions, Notifications & Reliable Background Work",
            dayRange = "Days 43–45",
            estimatedHours = 6,
            sourceBadge = "Modules 4 & 12",
            icon = "BellRing",
            subtopics = listOf(
                SubTopic(
                    id = "bg-1-services-receivers",
                    title = "Foreground & Bound Services, FGS Types & Broadcast Receivers",
                    description = "Started vs Bound vs Foreground Services, Android 14/15 `foregroundServiceType` declarations, Doze/App Standby restrictions, and Context-registered vs Manifest BroadcastReceivers.",
                    xp = 60,
                    resources = listOf(
                        StudyResource(
                            id = "r-bg-1",
                            title = "Foreground Services Overview (Official Docs)",
                            url = "https://developer.android.com/develop/background-work/services/foreground-services",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-bg-2",
                            title = "Broadcasts Overview & Security (Official Docs)",
                            url = "https://developer.android.com/develop/background-work/background-tasks/broadcasts",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "bg-2-workmanager",
                    title = "WorkManager Mastery: Constraints, Chaining, Expedited & HiltWorker",
                    description = "When to use WorkManager vs Coroutines vs Foreground Services vs AlarmManager; `OneTimeWorkRequest`, `PeriodicWorkRequest`, `ExistingWorkPolicy`, retry backoff, and `CoroutineWorker` with Hilt.",
                    xp = 70,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-bg-3",
                            title = "Background Work Decision Guide (Official)",
                            url = "https://developer.android.com/develop/background-work/background-tasks",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-bg-4",
                            title = "Getting Started with WorkManager (Official)",
                            url = "https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "bg-3-notifications",
                    title = "Modern Notifications, Channels, Permissions & Deep Link Actions",
                    description = "Android 13+ `POST_NOTIFICATIONS` runtime permission rationale UX, Notification Channels, Styles (MessagingStyle/BigPicture), grouping, and `TaskStackBuilder` synthetic back stacks.",
                    xp = 50,
                    resources = listOf(
                        StudyResource(
                            id = "r-bg-5",
                            title = "Create & Manage Notifications (Official Docs)",
                            url = "https://developer.android.com/develop/ui/views/notifications",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "How do you choose between Kotlin Coroutines, WorkManager, Foreground Service, and AlarmManager?",
                    keyPoints = "Coroutines: immediate in-process work tied to UI/scope. WorkManager: deferrable persistent work guaranteed to survive process death/reboots (sync, uploads). Foreground Service: user-visible ongoing work (audio, GPS navigation, active call). AlarmManager: exact wall-clock user-set alarms.",
                ),
            ),
        ),
        QuestModule(
            id = "q12-firebase-realtime",
            zoneId = ZoneId.SAPPHIRE,
            pillar = PillarId.ANDROID,
            order = 12,
            title = "Firebase & Real-Time Mobile Applications",
            subtitle = "Auth, Firestore, FCM Push Architecture, Remote Config & WebSockets/SSE",
            dayRange = "Days 46–47",
            estimatedHours = 5,
            sourceBadge = "Module 13",
            icon = "Flame",
            subtopics = listOf(
                SubTopic(
                    id = "fb-1-auth-firestore",
                    title = "Firebase Auth, Cloud Firestore Real-Time Streams & Crashlytics",
                    description = "Wrapping Firestore `SnapshotListener` inside Kotlin `callbackFlow`, offline persistence limits, Security Rules mindset, and Crashlytics custom keys + non-fatal logging.",
                    xp = 55,
                    resources = listOf(
                        StudyResource(
                            id = "r-fb-1",
                            title = "Firebase Android Documentation Hub",
                            url = "https://firebase.google.com/docs/android/setup",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "fb-2-fcm-websockets",
                    title = "FCM Push Delivery (Notification vs Data Payloads), Remote Config & WebSockets",
                    description = "Handling FCM tokens on reinstall/logout, Data vs Notification messages in background/killed states, Remote Config feature rollouts, and OkHttp WebSockets / SSE with heartbeats & reconnection.",
                    xp = 65,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-fb-2",
                            title = "Firebase Cloud Messaging on Android (Official)",
                            url = "https://firebase.google.com/docs/cloud-messaging/android/client",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-fb-3",
                            title = "YouTube: WebSockets & Real-Time Apps in Android (Ktor / OkHttp)",
                            url = "https://www.youtube.com/results?search_query=Android+WebSockets+OkHttp+Ktor+realtime+chat",
                            type = ResourceType.YOUTUBE,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "What is the difference between an FCM Notification message and a Data message when the app is in the background?",
                    keyPoints = "Notification messages are automatically displayed in the system tray by FCM SDK and `onMessageReceived` is NOT called until the user taps it. Data-only messages always invoke `onMessageReceived` so your app can process/sync/build a custom notification (subject to Doze priority).",
                ),
            ),
        ),
        QuestModule(
            id = "q13-boss-2",
            zoneId = ZoneId.SAPPHIRE,
            pillar = PillarId.ANDROID,
            order = 13,
            title = "BOSS BATTLE 2: Senior Offline-First Take-Home Review",
            subtitle = "Conquer the Sapphire Citadel with a clean multi-module offline-first architecture",
            dayRange = "Day 48",
            estimatedHours = 4,
            isBoss = true,
            highYield = true,
            sourceBadge = "Zone 2 Boss Battle",
            icon = "Swords",
            subtopics = listOf(
                SubTopic(
                    id = "b2-1-takehome-sim",
                    title = "Architect a Multi-Module Offline-First Feed Slice",
                    description = "Wire Compose UI + ViewModel (`StateFlow`) + Repository SSOT + Room + Retrofit/OkHttp + Hilt + WorkManager background sync with proper error handling and cancellation safety.",
                    xp = 175,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-b2-1",
                            title = "Now in Android Architecture Overview",
                            url = "https://github.com/android/nowinandroid/blob/main/docs/ArchitectureLearningJourney.md",
                            type = ResourceType.ARTICLE,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "Boss Prompt: Your product manager asks why offline support adds complexity. Walk through the failure modes you handle in your data layer.",
                    keyPoints = "Stale cache invalidation, race conditions between local mutations and incoming fetches, schema migrations without data loss, auth token expiry while offline, and retry/backoff limits in WorkManager.",
                ),
            ),
        ),

        // =========================================================================
        // ZONE 3: RUBY FORGE OF QUALITY & PRODUCTION
        // =========================================================================
        QuestModule(
            id = "q14-testing-quality",
            zoneId = ZoneId.RUBY,
            pillar = PillarId.ANDROID,
            order = 14,
            title = "Testing & Quality Engineering",
            subtitle = "Coroutines testing, Turbine, Fakes vs Mocks, Compose UI & Screenshot tests",
            dayRange = "Days 49–53",
            estimatedHours = 8,
            highYield = true,
            sourceBadge = "Senior US/EU Critical Upgrade",
            icon = "FlaskConical",
            subtopics = listOf(
                SubTopic(
                    id = "tst-1-unit-fakes",
                    title = "Android Testing Pyramid: JUnit 5, Fakes vs Mocks (MockK) & Test Doubles",
                    description = "Why Senior US/EU teams favor Fakes (e.g. `FakeUserRepository`) over brittle MockK verifications, dependency injection of Dispatchers/Clock, and fast JVM tests.",
                    xp = 75,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-tst-1",
                            title = "Testing Fundamentals on Android (Official Docs)",
                            url = "https://developer.android.com/training/testing/fundamentals",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-tst-2",
                            title = "Use Test Doubles & Fakes (Official Guide)",
                            url = "https://developer.android.com/training/testing/fundamentals/test-doubles",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "tst-2-coroutines-turbine",
                    title = "Testing Coroutines & Flows (runTest, TestDispatcher & Cash App Turbine)",
                    description = "`StandardTestDispatcher` vs `UnconfinedTestDispatcher`, `MainDispatcherRule`, virtual time (`advanceUntilIdle`, `advanceTimeBy`), and asserting `StateFlow`/`Flow` emissions cleanly with Turbine.",
                    xp = 80,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-tst-3",
                            title = "Testing Kotlin Coroutines on Android (Official)",
                            url = "https://developer.android.com/kotlin/coroutines/test",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-tst-4",
                            title = "Cash App Turbine (GitHub)",
                            url = "https://github.com/cashapp/turbine",
                            type = ResourceType.REPO,
                        ),
                    ),
                ),
                SubTopic(
                    id = "tst-3-compose-screenshot",
                    title = "Compose UI Testing, Robolectric & Screenshot Testing (Paparazzi/Roborazzi)",
                    description = "`createComposeRule()`, Semantics tree (`testTag` vs accessible labels), Robolectric JVM UI tests, and fast pixel-diff regression tests via Paparazzi / Roborazzi in CI.",
                    xp = 70,
                    resources = listOf(
                        StudyResource(
                            id = "r-tst-5",
                            title = "Testing Your Compose Layout (Official Docs)",
                            url = "https://developer.android.com/develop/ui/compose/testing",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-tst-6",
                            title = "Cash App Paparazzi Screenshot Testing (GitHub)",
                            url = "https://github.com/cashapp/paparazzi",
                            type = ResourceType.REPO,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "Why do many Senior Android engineers prefer Fakes over Mocking frameworks like Mockito/MockK?",
                    keyPoints = "Mocks tightly couple tests to implementation details (method call counts/order), making refactoring painful. Fakes implement the real interface with lightweight in-memory behavior (e.g. a `MutableStateFlow`), testing behavior instead of wiring.",
                ),
                InterviewQuestion(
                    question = "When should you use `StandardTestDispatcher` vs `UnconfinedTestDispatcher`?",
                    keyPoints = "`StandardTestDispatcher` queues coroutines and gives full deterministic control over execution order (ideal for testing Loading → Success intermediate states). `UnconfinedTestDispatcher` executes eagerly, useful for simple tests or collectors.",
                ),
            ),
        ),
        QuestModule(
            id = "q15-security-privacy",
            zoneId = ZoneId.RUBY,
            pillar = PillarId.ANDROID,
            order = 15,
            title = "Security, Privacy & Play Policy",
            subtitle = "Keystore, Biometrics, Network Security, Intent Hardening & Passkeys",
            dayRange = "Days 54–56",
            estimatedHours = 5,
            sourceBadge = "Senior US/EU Upgrade",
            icon = "ShieldCheck",
            subtopics = listOf(
                SubTopic(
                    id = "sec-1-keystore-biometrics",
                    title = "Android Keystore, Crypto + DataStore & BiometricPrompt",
                    description = "Hardware-backed AES-GCM keys in Android Keystore, encrypting DataStore values, CryptoObject with BiometricPrompt, and never hardcoding secrets in BuildConfig/APK.",
                    xp = 65,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-sec-1",
                            title = "Android Security Best Practices (Official)",
                            url = "https://developer.android.com/privacy-and-security/security-best-practices",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-sec-2",
                            title = "Android Keystore System (Official Docs)",
                            url = "https://developer.android.com/privacy-and-security/keystore",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "sec-2-network-components",
                    title = "Network Security Config, SSL Pinning, Intent/WebView Security & Credential Manager",
                    description = "Preventing cleartext traffic, certificate pinning trade-offs (rotation risks!), `android:exported`, Intent redirection prevention, Play Integrity API, and Credential Manager (Passkeys).",
                    xp = 65,
                    resources = listOf(
                        StudyResource(
                            id = "r-sec-3",
                            title = "Network Security Configuration (Official)",
                            url = "https://developer.android.com/privacy-and-security/security-config",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-sec-4",
                            title = "Credential Manager & Passkeys (Official)",
                            url = "https://developer.android.com/identity/sign-in/credential-manager",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "If you store an API key in `local.properties` and expose it via `BuildConfig`, is it secure?",
                    keyPoints = "No! Anyone can decompile the APK with jadx and read `BuildConfig`. True secrets must live on a backend proxy server, paired with short-lived user auth tokens and Play Integrity attestation.",
                ),
            ),
        ),
        QuestModule(
            id = "q16-production-readiness",
            zoneId = ZoneId.RUBY,
            pillar = PillarId.ANDROID,
            order = 16,
            title = "Production Readiness: i18n, Analytics & Accessibility",
            subtitle = "Multi-language & RTL, Analytics abstraction, Feature Flags & TalkBack A11y",
            dayRange = "Days 57–59",
            estimatedHours = 5,
            sourceBadge = "Modules 8 & 9 + A11y",
            icon = "Languages",
            subtopics = listOf(
                SubTopic(
                    id = "prod-1-i18n",
                    title = "Multi-Language (i18n/l10n), Plurals, RTL Layouts & Per-App Language",
                    description = "String resources, quantity plurals, locale-aware currency/date formatting (`NumberFormat`, `java.time`), `start`/`end` RTL mirroring, and Android 13+ Per-App Language Preferences.",
                    xp = 55,
                    resources = listOf(
                        StudyResource(
                            id = "r-prod-1",
                            title = "Support Different Languages & Cultures (Official)",
                            url = "https://developer.android.com/guide/topics/resources/localization",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-prod-2",
                            title = "Per-App Language Preferences (Official)",
                            url = "https://developer.android.com/guide/topics/resources/app-languages",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "prod-2-analytics",
                    title = "App Events & Analytics Architecture (Clean Abstraction + PII Hygiene)",
                    description = "Designing a provider-agnostic `AnalyticsTracker` interface (Firebase/Amplitude/Mixpanel), strongly typed event schemas, funnel/screen tracking, and never logging PII.",
                    xp = 55,
                    resources = listOf(
                        StudyResource(
                            id = "r-prod-3",
                            title = "Firebase Analytics for Android (Official)",
                            url = "https://firebase.google.com/docs/analytics/get-started?platform=android",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "prod-3-accessibility",
                    title = "Accessibility (a11y) in Compose: TalkBack, Semantics & Touch Targets",
                    description = "US/EU companies care deeply about ADA/EAA compliance: `semantics(mergeDescendants = true)`, 48dp minimum touch targets, dynamic font scaling (`sp`), and color contrast.",
                    xp = 55,
                    resources = listOf(
                        StudyResource(
                            id = "r-prod-4",
                            title = "Accessibility in Jetpack Compose (Official)",
                            url = "https://developer.android.com/develop/ui/compose/accessibility",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "How would you architect Analytics so your app can send events to both Firebase and Amplitude without polluting ViewModels?",
                    keyPoints = "Define a pure Kotlin `AnalyticsLogger` interface in `:core:analytics` with strongly typed sealed `AnalyticsEvent` models, inject a composite implementation that fans out to multiple SDK adapters, and log at navigation/action boundaries.",
                ),
            ),
        ),
        QuestModule(
            id = "q17-performance-profiling",
            zoneId = ZoneId.RUBY,
            pillar = PillarId.ANDROID,
            order = 17,
            title = "Performance, Memory & Profiling",
            subtitle = "LeakCanary, Perfetto traces, Cold Start, Baseline Profiles & R8 shrinking",
            dayRange = "Days 60–63",
            estimatedHours = 7,
            highYield = true,
            sourceBadge = "Module 15 + Senior Profiling",
            icon = "Gauge",
            subtopics = listOf(
                SubTopic(
                    id = "perf-1-memory-leaks",
                    title = "Memory Management, Heap Dumps & LeakCanary Mastery",
                    description = "GC Roots, generational heap, finding retained Activities/Views/CoroutineScopes with LeakCanary & Android Studio Memory Profiler, and Bitmap pooling/downsampling (Coil).",
                    xp = 75,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-perf-1",
                            title = "LeakCanary Fundamentals & Fixing Leaks",
                            url = "https://square.github.io/leakcanary/fundamentals/",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-perf-2",
                            title = "Overview of Memory Management on Android (Official)",
                            url = "https://developer.android.com/topic/performance/memory-overview",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "perf-2-startup-baseline",
                    title = "App Startup Optimization, Baseline Profiles & Macrobenchmark",
                    description = "Cold vs Warm vs Hot startup, TTID & TTFD metrics, deferring ContentProvider/SDK init in `Application.onCreate`, generating Baseline Profiles & verifying wins with Macrobenchmark.",
                    xp = 75,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-perf-3",
                            title = "App Startup Time Optimization (Official)",
                            url = "https://developer.android.com/topic/performance/vitals/launch-time",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-perf-4",
                            title = "Baseline Profiles Overview (Official)",
                            url = "https://developer.android.com/topic/performance/baselineprofiles/overview",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "perf-3-perfetto-r8",
                    title = "Perfetto System Tracing (Jank) & R8 / ProGuard Optimization",
                    description = "Reading frame timelines & main-thread slices in Perfetto UI, R8 full mode (tree shaking, obfuscation, keep rules, avoiding overly broad `-keep`), and APK Analyzer.",
                    xp = 70,
                    resources = listOf(
                        StudyResource(
                            id = "r-perf-5",
                            title = "Perfetto Trace Analysis Docs",
                            url = "https://perfetto.dev/docs/case-studies/android-boot-and-app-startup",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-perf-6",
                            title = "Shrink, Obfuscate, and Optimize with R8 (Official)",
                            url = "https://developer.android.com/build/shrink-code",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "Your app’s cold start time regressed from 800ms to 2.1s. How do you investigate and fix it?",
                    keyPoints = "Capture a Perfetto startup trace / Macrobenchmark on a release build. Look for heavy synchronous work on main thread in `Application.onCreate()`, ContentProviders, DI graph init, disk I/O, or missing Baseline Profiles. Lazy-init non-critical SDKs off the main thread.",
                ),
            ),
        ),
        QuestModule(
            id = "q18-cicd-publishing",
            zoneId = ZoneId.RUBY,
            pillar = PillarId.ANDROID,
            order = 18,
            title = "CI/CD, Release Engineering & Publishing",
            subtitle = "GitHub Actions, Detekt/ktlint, Signing, Staged Rollouts & Play Vitals",
            dayRange = "Days 64–66",
            estimatedHours = 5,
            sourceBadge = "Modules 14 & 15",
            icon = "Rocket",
            subtopics = listOf(
                SubTopic(
                    id = "ci-1-pipelines",
                    title = "CI/CD Pipelines (GitHub Actions), Static Analysis & Build Caching",
                    description = "Automated PR checks (ktlint, Detekt, Android Lint, Unit & Screenshot tests), Gradle remote/local caching, Fastlane / Play Publisher plugin, and automated release tracks.",
                    xp = 65,
                    resources = listOf(
                        StudyResource(
                            id = "r-ci-1",
                            title = "GitHub Actions Setup for Android & Gradle",
                            url = "https://github.com/gradle/actions",
                            type = ResourceType.REPO,
                        ),
                        StudyResource(
                            id = "r-ci-2",
                            title = "Detekt Static Code Analysis for Kotlin",
                            url = "https://detekt.dev/",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "ci-2-release-vitals",
                    title = "Play Store Signing, Staged Rollouts, Feature Flags & Android Vitals",
                    description = "Play App Signing, uploading R8 mapping files, staged rollouts (1% → 10% → 50% → 100%) with kill-switch feature flags, In-App Updates API, and monitoring Crash-free / ANR thresholds.",
                    xp = 60,
                    resources = listOf(
                        StudyResource(
                            id = "r-ci-3",
                            title = "Android Vitals & Release Quality (Official)",
                            url = "https://developer.android.com/topic/performance/vitals",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "How do you ship risky new features safely to millions of Android users without waiting days for Play Store review on a hotfix?",
                    keyPoints = "Wrap features behind Remote Config / feature flags with kill switches, ship dark-launched code behind tests, use staged rollouts (1% → 5% → 20% → 100%) watching Crashlytics/ANR alerts, and prompt critical fixes via Google Play In-App Updates.",
                ),
            ),
        ),
        QuestModule(
            id = "q19-capstone-project",
            zoneId = ZoneId.RUBY,
            pillar = PillarId.ANDROID,
            order = 19,
            title = "Capstone Portfolio Project",
            subtitle = "Ship one standout, open-source Senior-grade Android app on GitHub",
            dayRange = "Days 67–71",
            estimatedHours = 12,
            sourceBadge = "Module 16",
            icon = "FolderGit2",
            subtopics = listOf(
                SubTopic(
                    id = "cap-1-architecture-build",
                    title = "Build Multi-Module Offline-First Jetpack Compose Showcase App",
                    description = "Include `:app`, `:core:*`, `:feature:*`, Convention Plugins, Coroutines/Flow, Room + Paging 3, Hilt, WorkManager sync, Baseline Profile, and Dark/Light + Adaptive UI.",
                    xp = 120,
                    resources = listOf(
                        StudyResource(
                            id = "r-cap-1",
                            title = "Now in Android (Architecture Benchmark)",
                            url = "https://github.com/android/nowinandroid",
                            type = ResourceType.REPO,
                        ),
                    ),
                ),
                SubTopic(
                    id = "cap-2-tests-ci-readme",
                    title = "Add Unit/Turbine/Screenshot Tests, GitHub Actions CI & Architecture README",
                    description = "US/EU hiring managers LOVE clean READMEs with Mermaid architecture diagrams, GIF demos, trade-off explanations, passing CI badge, and clean commit history.",
                    xp = 100,
                    resources = listOf(
                        StudyResource(
                            id = "r-cap-2",
                            title = "Awesome README Examples for Inspiration",
                            url = "https://github.com/matiassingers/awesome-readme",
                            type = ResourceType.REPO,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "Walk me through the hardest technical trade-off you made in your portfolio or production app.",
                    keyPoints = "Prepare a 3-minute structured explanation: Context → Problem/Constraint → Options Considered → Chosen Architecture → Measurable Result & What you would improve next.",
                ),
            ),
        ),
        QuestModule(
            id = "q20-boss-3",
            zoneId = ZoneId.RUBY,
            pillar = PillarId.ANDROID,
            order = 20,
            title = "BOSS BATTLE 3: Debugging & Senior PR Review Gauntlet",
            subtitle = "Master production debugging, memory leak triage, and constructive PR reviews",
            dayRange = "Day 72",
            estimatedHours = 3,
            isBoss = true,
            highYield = true,
            sourceBadge = "Zone 3 Boss Battle",
            icon = "Swords",
            subtopics = listOf(
                SubTopic(
                    id = "b3-1-pr-review",
                    title = "Practice a Senior PR Review & Production Incident Postmortem in English",
                    description = "Review a real open-source Android PR focusing on concurrency bugs, recomposition traps, testability, accessibility, and empathetic senior-level review comments.",
                    xp = 150,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-b3-1",
                            title = "Google Engineering Practices: How to Do a Code Review",
                            url = "https://google.github.io/eng-practices/review/reviewer/",
                            type = ResourceType.ARTICLE,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "Boss Prompt: A junior engineer opens a PR that launches `GlobalScope.launch` inside a Composable to call Retrofit and updates a `var` list directly. How do you review it?",
                    keyPoints = "Be constructive and explain the \"why\": GlobalScope leaks past screen lifecycle, network calls belong in Repository/ViewModel, UI needs immutable `StateFlow` collected with `collectAsStateWithLifecycle()`, and offer a clean snippet.",
                ),
            ),
        ),
    )
}
