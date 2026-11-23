package com.lrds.androidroadapp.presentation

import androidx.compose.runtime.Immutable

@Immutable
data class StableList<T>(val items: List<T> = emptyList()) : List<T> by items

@Immutable
data class StableSet<T>(val items: Set<T> = emptySet()) : Set<T> by items

@Immutable
data class StableMap<K, V>(val entriesMap: Map<K, V> = emptyMap()) : Map<K, V> by entriesMap

fun <T> List<T>.toStableList(): StableList<T> = StableList(this)

fun <T> Set<T>.toStableSet(): StableSet<T> = StableSet(this)

fun <K, V> Map<K, V>.toStableMap(): StableMap<K, V> = StableMap(this)
