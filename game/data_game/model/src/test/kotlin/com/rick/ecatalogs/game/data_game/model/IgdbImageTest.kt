package com.rick.ecatalogs.game.data_game.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class IgdbImageTest {

    @Test
    fun imageUrl_usesTheSizeAndImageId() {
        assertEquals(
            "https://images.igdb.com/igdb/image/upload/t_screenshot_big/abc123.jpg",
            igdbImageUrl("abc123", IgdbImageSize.SCREENSHOT_BIG),
        )
    }

    @Test
    fun gameWithoutCover_hasNoCoverUrl() {
        val game = Game(1, "Game", null, "", null, 0, null, emptyList(), emptyList())
        assertNull(game.coverUrl())
    }

    @Test
    fun favorites_markTheUserGames() {
        val games = listOf(1, 2).map { Game(it, "Game $it", null, "", null, 0, null, emptyList(), emptyList()) }
        val userGames = games.asUserGames(GameUserData(gameFavoriteIds = setOf(2)))
        assertEquals(listOf(false, true), userGames.map { it.isFavorite })
    }
}
