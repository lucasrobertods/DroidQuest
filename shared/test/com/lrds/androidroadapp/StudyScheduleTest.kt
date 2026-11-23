package com.lrds.androidroadapp

import com.lrds.androidroadapp.domain.model.StudySchedule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StudyScheduleTest {
    @Test
    fun `normalizes and encodes weekday sets deterministically`() {
        assertEquals(setOf(1, 3, 5), StudySchedule.normalizeDays(setOf(5, 1, 3, 0, 8, -1)))
        assertEquals("1,3,5", StudySchedule.encodeDaysCsv(setOf(5, 1, 3)))
        assertEquals(setOf(1, 2, 3, 4, 5), StudySchedule.decodeDaysCsv(" 5, 1,2, 3, 4, 99, foo "))
        assertEquals(emptySet(), StudySchedule.decodeDaysCsv(""))
    }

    @Test
    fun `formats time and readable weekday summaries`() {
        assertEquals("07:05", StudySchedule.formatTime(7, 5))
        assertEquals("23:59", StudySchedule.formatTime(29, 99))
        assertEquals("00:00", StudySchedule.formatTime(-3, -10))

        assertEquals("No days selected", StudySchedule.formatDaysSummary(emptySet()))
        assertEquals("Mon–Fri", StudySchedule.formatDaysSummary(setOf(1, 2, 3, 4, 5)))
        assertEquals("Weekends", StudySchedule.formatDaysSummary(setOf(6, 7)))
        assertEquals("Every day", StudySchedule.formatDaysSummary(setOf(1, 2, 3, 4, 5, 6, 7)))
        assertEquals("Mon, Wed, Fri", StudySchedule.formatDaysSummary(setOf(5, 1, 3)))
    }

    @Test
    fun `maps ISO-8601 weekdays to Gregorian Calendar and Apple weekday numbering`() {
        assertEquals(2, StudySchedule.isoDayToGregorianWeekday(1)) // Monday
        assertEquals(3, StudySchedule.isoDayToGregorianWeekday(2)) // Tuesday
        assertEquals(4, StudySchedule.isoDayToGregorianWeekday(3)) // Wednesday
        assertEquals(5, StudySchedule.isoDayToGregorianWeekday(4)) // Thursday
        assertEquals(6, StudySchedule.isoDayToGregorianWeekday(5)) // Friday
        assertEquals(7, StudySchedule.isoDayToGregorianWeekday(6)) // Saturday
        assertEquals(1, StudySchedule.isoDayToGregorianWeekday(7)) // Sunday
        assertFailsWith<IllegalArgumentException> { StudySchedule.isoDayToGregorianWeekday(0) }
        assertFailsWith<IllegalArgumentException> { StudySchedule.isoDayToGregorianWeekday(8) }
    }
}
