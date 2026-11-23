package com.lrds.androidroadapp.domain.model

private const val ISO_MONDAY = 1
private const val ISO_TUESDAY = 2
private const val ISO_WEDNESDAY = 3
private const val ISO_THURSDAY = 4
private const val ISO_FRIDAY = 5
private const val ISO_SATURDAY = 6
private const val ISO_SUNDAY = 7
private const val TOTAL_WEEKDAYS = 7

private const val MIN_HOUR = 0
private const val MAX_HOUR = 23
private const val MIN_MINUTE = 0
private const val MAX_MINUTE = 59
private const val TIME_PAD_WIDTH = 2

private const val GREGORIAN_SUNDAY = 1

/**
 * ISO-8601 weekday utilities for Study Session Reminders (`1 = Monday` .. `7 = Sunday`).
 */
object StudySchedule {
    const val DEFAULT_HOUR: Int = 19
    const val DEFAULT_MINUTE: Int = 0
    val DEFAULT_DAYS: Set<Int> = setOf(ISO_MONDAY, ISO_TUESDAY, ISO_WEDNESDAY, ISO_THURSDAY, ISO_FRIDAY)
    const val DEFAULT_DAYS_CSV: String = "1,2,3,4,5"

    val ALL_ISO_DAYS: List<Int> = listOf(
        ISO_MONDAY,
        ISO_TUESDAY,
        ISO_WEDNESDAY,
        ISO_THURSDAY,
        ISO_FRIDAY,
        ISO_SATURDAY,
        ISO_SUNDAY,
    )

    private val WEEKEND_DAYS: Set<Int> = setOf(ISO_SATURDAY, ISO_SUNDAY)

    fun normalizeDays(days: Set<Int>): Set<Int> =
        days.filter { it in ISO_MONDAY..ISO_SUNDAY }.sorted().toSet()

    fun clampHour(hour: Int): Int = hour.coerceIn(MIN_HOUR, MAX_HOUR)

    fun clampMinute(minute: Int): Int = minute.coerceIn(MIN_MINUTE, MAX_MINUTE)

    fun encodeDaysCsv(days: Set<Int>): String =
        normalizeDays(days).joinToString(",")

    fun decodeDaysCsv(raw: String): Set<Int> {
        if (raw.isBlank()) return emptySet()
        return raw.split(',')
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it in ISO_MONDAY..ISO_SUNDAY }
            .sorted()
            .toSet()
    }

    fun formatTime(hour: Int, minute: Int): String {
        val h = clampHour(hour).toString().padStart(TIME_PAD_WIDTH, '0')
        val m = clampMinute(minute).toString().padStart(TIME_PAD_WIDTH, '0')
        return "$h:$m"
    }

    fun dayInitial(isoDay: Int): String = when (isoDay) {
        ISO_MONDAY -> "M"
        ISO_TUESDAY -> "T"
        ISO_WEDNESDAY -> "W"
        ISO_THURSDAY -> "T"
        ISO_FRIDAY -> "F"
        ISO_SATURDAY -> "S"
        ISO_SUNDAY -> "S"
        else -> "?"
    }

    fun dayShortName(isoDay: Int): String = when (isoDay) {
        ISO_MONDAY -> "Mon"
        ISO_TUESDAY -> "Tue"
        ISO_WEDNESDAY -> "Wed"
        ISO_THURSDAY -> "Thu"
        ISO_FRIDAY -> "Fri"
        ISO_SATURDAY -> "Sat"
        ISO_SUNDAY -> "Sun"
        else -> "?"
    }

    fun formatDaysSummary(days: Set<Int>): String {
        val normalized = normalizeDays(days)
        return when {
            normalized.isEmpty() -> "No days selected"
            normalized.size == TOTAL_WEEKDAYS -> "Every day"
            normalized == DEFAULT_DAYS -> "Mon–Fri"
            normalized == WEEKEND_DAYS -> "Weekends"
            else -> normalized.joinToString(", ") { dayShortName(it) }
        }
    }

    /**
     * Converts ISO-8601 weekday (`1 = Monday` .. `7 = Sunday`) to Gregorian Calendar / Apple
     * `NSDateComponents.weekday` numbering (`1 = Sunday`, `2 = Monday`, ..., `7 = Saturday`).
     */
    fun isoDayToGregorianWeekday(isoDay: Int): Int {
        require(isoDay in ISO_MONDAY..ISO_SUNDAY) {
            "ISO day of week must be in 1..7, got $isoDay"
        }
        return if (isoDay == ISO_SUNDAY) GREGORIAN_SUNDAY else isoDay + 1
    }
}
