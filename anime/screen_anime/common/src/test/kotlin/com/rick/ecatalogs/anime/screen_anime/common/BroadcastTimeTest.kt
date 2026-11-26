package com.rick.ecatalogs.anime.screen_anime.common

import com.rick.ecatalogs.anime.data_anime.model.browse.Broadcast
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BroadcastTimeTest {

    private val utc = ZoneId.of("UTC")

    @Test
    fun tokyoSlot_isShownInTheUsersZone() {
        val slot = Broadcast("Saturdays", "23:30", "Asia/Tokyo").inZone(utc)!!

        assertEquals(DayOfWeek.SATURDAY, slot.dayOfWeek)
        assertEquals(LocalTime.of(14, 30), slot.toLocalTime())
        assertEquals(utc, slot.zone)
    }

    @Test
    fun earlyMorningInTokyo_isThePreviousDayInEurope() {
        val slot = Broadcast("Sundays", "01:00", "Asia/Tokyo").inZone(utc)!!

        assertEquals(DayOfWeek.SATURDAY, slot.dayOfWeek)
        assertEquals(LocalTime.of(16, 0), slot.toLocalTime())
    }

    @Test
    fun lateEveningInTokyo_isTheSameDayInTheAmericas() {
        // Tokyo is 14 h ahead of New York in winter and 13 h in summer
        val slot = Broadcast("Fridays", "23:00", "Asia/Tokyo").inZone(ZoneId.of("America/New_York"))!!

        assertEquals(DayOfWeek.FRIDAY, slot.dayOfWeek)
    }

    @Test
    fun jikanSpacingAndCase_areAccepted() {
        val slot = Broadcast(" mondays ", " 12:00 ", "Asia/Tokyo").inZone(ZoneId.of("Asia/Tokyo"))!!

        assertEquals(DayOfWeek.MONDAY, slot.dayOfWeek)
        assertEquals(LocalTime.NOON, slot.toLocalTime())
    }

    @Test
    fun unreadableValues_giveNull() {
        assertNull(Broadcast("Unknown", "23:30", "Asia/Tokyo").inZone(utc))
        assertNull(Broadcast("Mondays", "late night", "Asia/Tokyo").inZone(utc))
        assertNull(Broadcast("Mondays", "23:30", "Not/AZone").inZone(utc))
    }

    @Test
    fun jikanScheduleFilter_isTheLowercaseDayName() {
        assertEquals("monday", DayOfWeek.MONDAY.jikanName)
        assertEquals(
            listOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"),
            DayOfWeek.entries.map { it.jikanName },
        )
    }
}
