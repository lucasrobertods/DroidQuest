package com.lrds.androidroadapp

import com.lrds.androidroadapp.platform.computeNextWeeklyTriggerMillis
import java.util.Calendar
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlatformAndroidTest {
    @Test
    fun `platform name is reported`() {
        assertTrue(getPlatform().name.isNotBlank())
    }

    @Test
    fun `computeNextWeeklyTriggerMillis schedules same-day future time and rolls past time by 7 days`() {
        val utc = TimeZone.getTimeZone("UTC")
        // Monday 2026-11-23 at 14:00:00 UTC
        val mondayAfternoon = Calendar.getInstance(utc).apply {
            set(2026, Calendar.NOVEMBER, 23, 14, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // Same day (Monday = ISO 1) at 19:30 -> stays on 2026-11-23
        val sameDayTrigger = computeNextWeeklyTriggerMillis(
            isoDayOfWeek = 1,
            hour = 19,
            minute = 30,
            nowMillis = mondayAfternoon,
            timeZone = utc,
        )
        val sameDayCal = Calendar.getInstance(utc).apply { timeInMillis = sameDayTrigger }
        assertEquals(2026, sameDayCal.get(Calendar.YEAR))
        assertEquals(Calendar.NOVEMBER, sameDayCal.get(Calendar.MONTH))
        assertEquals(23, sameDayCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(19, sameDayCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(30, sameDayCal.get(Calendar.MINUTE))

        // Same day (Monday = ISO 1) at 09:00 -> already passed, rolls to next Monday 2026-11-30
        val nextWeekTrigger = computeNextWeeklyTriggerMillis(
            isoDayOfWeek = 1,
            hour = 9,
            minute = 0,
            nowMillis = mondayAfternoon,
            timeZone = utc,
        )
        val nextWeekCal = Calendar.getInstance(utc).apply { timeInMillis = nextWeekTrigger }
        assertEquals(30, nextWeekCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(Calendar.MONDAY, nextWeekCal.get(Calendar.DAY_OF_WEEK))
        assertEquals(9, nextWeekCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, nextWeekCal.get(Calendar.MINUTE))

        // Sunday (ISO 7) from Monday 2026-11-23 -> Sunday 2026-11-29
        val sundayTrigger = computeNextWeeklyTriggerMillis(
            isoDayOfWeek = 7,
            hour = 18,
            minute = 45,
            nowMillis = mondayAfternoon,
            timeZone = utc,
        )
        val sundayCal = Calendar.getInstance(utc).apply { timeInMillis = sundayTrigger }
        assertEquals(29, sundayCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(Calendar.SUNDAY, sundayCal.get(Calendar.DAY_OF_WEEK))
        assertEquals(18, sundayCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(45, sundayCal.get(Calendar.MINUTE))
    }
}
