package com.rick.ecatalogs.game.data_game.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class GameRecentSearchQuery(
    val query: String,
    val queriedDate: Instant = Clock.System.now(),
)
