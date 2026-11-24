package com.rick.ecatalogs.anime.screen_anime.anime_details

import com.rick.ecatalogs.anime.data_anime.model.browse.Broadcast
import org.junit.Test
import java.time.ZoneId
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LocalBroadcastTest {

    private val utc = ZoneId.of("UTC")

    @Test
    fun japanTime_isConvertedToTheUsersZone() {
        val (day, time) = localBroadcast(Broadcast("Saturdays", "23:30", "Asia/Tokyo"), utc, Locale.ENGLISH)!!
        assertEquals("Saturday", day)
        assertTrue(time.startsWith("2:30"), time) // 14:30 UTC
    }

    @Test
    fun earlyJapaneseSlot_fallsOnThePreviousDayElsewhere() {
        val (day, _) = localBroadcast(Broadcast("Sundays", "01:00", "Asia/Tokyo"), utc, Locale.ENGLISH)!!
        assertEquals("Saturday", day) // 16:00 UTC the day before
    }

    @Test
    fun unreadableValues_giveNull() {
        assertNull(localBroadcast(Broadcast("Unknown", "23:30", "Asia/Tokyo"), utc))
        assertNull(localBroadcast(Broadcast("Mondays", "late night", "Asia/Tokyo"), utc))
    }
}
