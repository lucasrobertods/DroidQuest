package com.lrds.androidroadapp.domain.model

/**
 * ISO-8601 weekday utilities for Study Session Reminders (`1 = Monday` .. `7 = Sunday`).
 */
object StudySchedule {
    const val DEFAULT_HOUR: Int = 19
    const val DEFAULT_MINUTE: Int = 0
    val DEFAULT_DAYS: Set<Int> = setOf(1, 2, 3, 4, 5) // Mon–Fri
    const val DEFAULT_DAYS_CSV: String = "1,2,3,4,5"

    val ALL_ISO_DAYS: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7)

    fun normalizeDays(days: Set<Int>): Set<Int> =
        days.filter { it in 1..7 }.sorted().toSet()

    fun clampHour(hour: Int): Int = hour.coerceIn(0, 23)

    fun clampMinute(minute: Int): Int = minute.coerceIn(0, 59)

    fun encodeDaysCsv(days: Set<Int>): String =
        normalizeDays(days).joinToString(",")

    fun decodeDaysCsv(raw: String): Set<Int> {
        if (raw.isBlank()) return emptySet()
        return raw.split(',')
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it in 1..7 }
            .sorted()
            .toSet()
    }

    fun formatTime(hour: Int, minute: Int): String {
        val h = clampHour(hour).toString().padStart(2, '0')
        val m = clampMinute(minute).toString().padStart(2, '0')
        return "$h:$m"
    }

    fun dayInitial(isoDay: Int): String = when (isoDay) {
        1 -> "M"
        2 -> "T"
        3 -> "W"
        4 -> "T"
        5 -> "F"
        6 -> "S"
        7 -> "S"
        else -> "?"
    }

    fun dayShortName(isoDay: Int): String = when (isoDay) {
        1 -> "Mon"
        2 -> "Tue"
        3 -> "Wed"
        4 -> "Thu"
        5 -> "Fri"
        6 -> "Sat"
        7 -> "Sun"
        else -> "?"
    }

    fun formatDaysSummary(days: Set<Int>): String {
        val normalized = normalizeDays(days)
        return when {
            normalized.isEmpty() -> "No days selected"
            normalized.size == 7 -> "Every day"
            normalized == setOf(1, 2, 3, 4, 5) -> "Mon–Fri"
            normalized == setOf(6, 7) -> "Weekends"
            else -> normalized.joinToString(", ") { dayShortName(it) }
        }
    }

    /**
     * Converts ISO-8601 weekday (`1 = Monday` .. `7 = Sunday`) to Gregorian Calendar / Apple
     * `NSDateComponents.weekday` numbering (`1 = Sunday`, `2 = Monday`, ..., `7 = Saturday`).
     */
    fun isoDayToGregorianWeekday(isoDay: Int): Int {
        require(isoDay in 1..7) { "ISO day of week must be in 1..7, got $isoDay" }
        return if (isoDay == 7) 1 else isoDay + 1
    }
}
