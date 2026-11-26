package com.rick.ecatalogs.game.data_game.network

import com.rick.ecatalogs.game.data_game.network.model.IgdbGameNetwork
import com.rick.ecatalogs.game.data_game.network.model.IgdbNamedNetwork

interface IgdbNetworkDataSource {

    /** False when the IGDB credentials aren't set (prod without igdbClientId / igdbClientSecret). */
    val isConfigured: Boolean

    /** The most popular games right now, in popularity order; [page] starts at 1. Empty past the end. */
    suspend fun fetchPopularGames(page: Int): List<IgdbGameNetwork>

    /** Hyped games coming out in the next months, soonest first. [now] is epoch seconds. */
    suspend fun fetchComingSoon(now: Long): List<IgdbGameNetwork>

    /** Hyped games released in the last weeks, newest first. [now] is epoch seconds. */
    suspend fun fetchRecentlyReleased(now: Long): List<IgdbGameNetwork>

    suspend fun fetchGenres(): List<IgdbNamedNetwork>

    /** Most rated games of a genre; [page] starts at 1. */
    suspend fun fetchGamesByGenre(genreId: Int, page: Int): List<IgdbGameNetwork>

    suspend fun fetchGameDetails(id: Int): IgdbGameNetwork?

    suspend fun searchGames(query: String): List<IgdbGameNetwork>
}
