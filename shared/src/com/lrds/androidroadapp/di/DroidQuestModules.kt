package com.lrds.androidroadapp.di

import com.lrds.androidroadapp.data.ProgressRepository
import com.lrds.androidroadapp.data.ProgressStorage
import com.lrds.androidroadapp.data.createPlatformProgressStorage
import com.lrds.androidroadapp.platform.PlatformBridge
import com.lrds.androidroadapp.platform.createPlatformBridge
import com.lrds.androidroadapp.presentation.DroidQuestViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.datetime.TimeZone
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
val droidQuestModule = module {
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    single<Clock> { Clock.System }
    single<ProgressStorage> { createPlatformProgressStorage() }
    single<PlatformBridge> { createPlatformBridge() }
    single {
        ProgressRepository(
            storage = get(),
            scope = get(),
            clock = get(),
            timeZoneProvider = { TimeZone.currentSystemDefault() },
        )
    }
    viewModel {
        DroidQuestViewModel(
            repository = get(),
            platformBridge = get(),
            clock = get(),
            timeZoneProvider = { TimeZone.currentSystemDefault() },
        )
    }
}

fun ensureKoinStarted(): KoinApplication {
    val existing = KoinPlatformTools.defaultContext().getOrNull()
    if (existing != null) {
        return KoinApplication.init().apply {
            // Already started globally
        }
    }
    return startKoin {
        modules(droidQuestModule)
    }
}
