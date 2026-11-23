package com.lrds.androidroadapp.domain.curriculum

import com.lrds.androidroadapp.domain.model.AdventureZone
import com.lrds.androidroadapp.domain.model.ZoneId

object ZonesCatalog {
    val zones: List<AdventureZone> = listOf(
        AdventureZone(
            id = ZoneId.EMERALD,
            number = 1,
            title = "Emerald Foundations & Modern UI",
            shortTitle = "Emerald Foundations",
            subtitle = "Android Core, Kotlin Internals, Navigation & Jetpack Compose Mastery",
            daySpan = "Days 1–20",
            badgeTitle = "Compose Ranger",
            badgeIcon = "Compass",
        ),
        AdventureZone(
            id = ZoneId.SAPPHIRE,
            number = 2,
            title = "Sapphire Citadel of Architecture & Data",
            shortTitle = "Sapphire Citadel",
            subtitle = "Coroutines, Flow, MVVM/MVI, Modularization, DI, Offline-First, Networking & Background Work",
            daySpan = "Days 21–48",
            badgeTitle = "Citadel Architect",
            badgeIcon = "Layers",
        ),
        AdventureZone(
            id = ZoneId.RUBY,
            number = 3,
            title = "Ruby Forge of Quality & Production",
            shortTitle = "Ruby Forge",
            subtitle = "Testing, Security, i18n, Analytics, Perfetto Profiling, CI/CD & Capstone App",
            daySpan = "Days 49–72",
            badgeTitle = "Production Guardian",
            badgeIcon = "ShieldCheck",
        ),
        AdventureZone(
            id = ZoneId.GOLDEN,
            number = 4,
            title = "Golden Labyrinth of DSA & Algorithms",
            shortTitle = "Golden Labyrinth",
            subtitle = "Your 6 DSA Course Tracks + Big Tech Coding Patterns in Kotlin (Run in Parallel Daily!)",
            daySpan = "Daily Parallel • Days 1–90",
            badgeTitle = "Algorithm Alchemist",
            badgeIcon = "Sparkles",
        ),
        AdventureZone(
            id = ZoneId.COSMIC,
            number = 5,
            title = "Cosmic Mobile System Design & Global Summit",
            shortTitle = "Cosmic Summit",
            subtitle = "Your SD Course + Senior Mobile Architecture Case Studies + US/EU/Big Tech Offers",
            daySpan = "Days 73–100 (+ Active Interviews)",
            badgeTitle = "Global Offer Legend",
            badgeIcon = "Trophy",
        ),
    )
}
