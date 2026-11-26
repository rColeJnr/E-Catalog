package com.rick.ecatalogs.game.data_game.data

import com.rick.ecatalogs.game.data_game.data.model.asGame
import com.rick.ecatalogs.game.data_game.data.model.asGameDetails
import com.rick.ecatalogs.game.data_game.network.model.IgdbGameNetwork
import com.rick.ecatalogs.game.data_game.network.model.IgdbImageNetwork
import com.rick.ecatalogs.game.data_game.network.model.IgdbInvolvedCompanyNetwork
import com.rick.ecatalogs.game.data_game.network.model.IgdbNamedNetwork
import com.rick.ecatalogs.game.data_game.network.model.IgdbPlatformNetwork
import com.rick.ecatalogs.game.data_game.network.model.IgdbVideoNetwork
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GameMappersTest {

    @Test
    fun missingFields_becomeEmptyValues() {
        val game = IgdbGameNetwork(id = 7).asGame()
        assertEquals("", game.name)
        assertNull(game.coverImageId)
        assertEquals(0, game.ratingCount)
        assertEquals(emptyList(), game.genres)
    }

    @Test
    fun platforms_preferTheAbbreviation() {
        val game = IgdbGameNetwork(
            id = 1,
            platforms = listOf(
                IgdbPlatformNetwork(6, name = "PC (Microsoft Windows)", abbreviation = "PC"),
                IgdbPlatformNetwork(9, name = "Some console", abbreviation = null),
            ),
        ).asGame()
        assertEquals(listOf("PC", "Some console"), game.platforms)
    }

    @Test
    fun details_pickATrailer_andSplitCompaniesByRole() {
        val details = IgdbGameNetwork(
            id = 1,
            name = "Game",
            cover = IgdbImageNetwork("cover"),
            videos = listOf(IgdbVideoNetwork("Gameplay", "gameplay"), IgdbVideoNetwork("Launch Trailer", "trailer")),
            involvedCompanies = listOf(
                IgdbInvolvedCompanyNetwork(IgdbNamedNetwork(1, "Studio"), developer = true, publisher = false),
                IgdbInvolvedCompanyNetwork(IgdbNamedNetwork(2, "Publisher"), developer = false, publisher = true),
            ),
            similarGames = listOf(IgdbGameNetwork(id = 2, name = "Other"), IgdbGameNetwork(id = 3)),
        ).asGameDetails()
        assertEquals("trailer", details.trailerYoutubeId)
        assertEquals(listOf("Studio"), details.developers)
        assertEquals(listOf("Publisher"), details.publishers)
        // Similar games without a name are left out
        assertEquals(listOf(2), details.similar.map { it.id })
    }
}
