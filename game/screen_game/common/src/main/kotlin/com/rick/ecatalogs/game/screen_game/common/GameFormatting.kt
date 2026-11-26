package com.rick.ecatalogs.game.screen_game.common

import com.rick.ecatalogs.game.data_game.model.Game
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/** "2024", or null when IGDB has no date. */
fun Game.releaseYear(): String? = releaseDate?.let {
    Instant.ofEpochSecond(it).atZone(ZoneOffset.UTC).year.toString()
}

/** "Mar 3, 2025" in the phone's language. */
fun Game.releaseDateText(locale: Locale = Locale.getDefault()): String? = releaseDate?.let {
    Instant.ofEpochSecond(it).atZone(ZoneOffset.UTC).toLocalDate()
        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale))
}

/** IGDB ratings are 0–100; shown out of 10 like the other catalogs ("★ 8.7"). */
fun Game.ratingText(): String? = rating?.takeIf { ratingCount > 0 }?.let {
    "★ " + String.format(Locale.getDefault(), "%.1f", it / 10)
}

/** "8.7 · 2024 · PC, PS5" for list subtitles. */
fun Game.subtitle(): String = listOfNotNull(
    ratingText(),
    releaseYear(),
    platforms.take(MAX_PLATFORMS).joinToString().takeIf { it.isNotEmpty() },
).joinToString(" · ")

private const val MAX_PLATFORMS = 3
