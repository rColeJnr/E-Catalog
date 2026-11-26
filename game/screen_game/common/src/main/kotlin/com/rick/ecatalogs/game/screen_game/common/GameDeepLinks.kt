package com.rick.ecatalogs.game.screen_game.common

import android.net.Uri

/** Deep links of the game screens (each screen is its own module, so they link by URI). */
object GameDeepLinks {
    const val CATALOG = "com.rick.ecs://game_catalog"
    const val SEARCH = "com.rick.ecs://game_search"
    const val FAVORITES = "com.rick.ecs://game_favorites"

    fun details(gameId: Int): Uri = Uri.parse("com.rick.ecs://game_details/$gameId")
}
