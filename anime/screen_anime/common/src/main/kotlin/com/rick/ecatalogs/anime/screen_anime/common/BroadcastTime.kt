package com.rick.ecatalogs.anime.screen_anime.common

import com.rick.ecatalogs.anime.data_anime.model.browse.Broadcast
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import java.util.Locale

/**
 * The next broadcast slot, converted from Japan time (Jikan's zone) to [zone]: e.g. a Sunday
 * 01:00 slot in Tokyo is Saturday afternoon in Lisbon. Null if Jikan's values can't be read.
 */
fun Broadcast.inZone(zone: ZoneId = ZoneId.systemDefault()): ZonedDateTime? = runCatching {
    // "Saturdays" -> SATURDAY
    val day = DayOfWeek.valueOf(day.trim().removeSuffix("s").uppercase(Locale.ROOT))
    ZonedDateTime.now(ZoneId.of(timezone))
        .with(TemporalAdjusters.nextOrSame(day))
        .with(LocalTime.parse(time.trim()))
        .withZoneSameInstant(zone)
}.getOrNull()

/** Jikan's schedule filter for a day of the week, e.g. "monday". */
val DayOfWeek.jikanName: String get() = name.lowercase(Locale.ROOT)
