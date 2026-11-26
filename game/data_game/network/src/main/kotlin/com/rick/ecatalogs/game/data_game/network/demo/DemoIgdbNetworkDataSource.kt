package com.rick.ecatalogs.game.data_game.network.demo

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rick.ecatalogs.game.data_game.network.IgdbNetworkDataSource
import com.rick.ecatalogs.game.data_game.network.model.IgdbGameNetwork
import com.rick.ecatalogs.game.data_game.network.model.IgdbNamedNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject

fun interface DemoIgdbAssetManager {
    fun open(filename: String): InputStream
}

/** Demo flavor: the same few games from `assets/games.json` for every list, no network. */
class DemoIgdbNetworkDataSource @Inject constructor(
    private val assets: DemoIgdbAssetManager,
) : IgdbNetworkDataSource {

    private val gson = Gson()

    override val isConfigured = true

    private suspend fun games(): List<IgdbGameNetwork> = withContext(Dispatchers.IO) {
        assets.open(GAMES_ASSET).bufferedReader().use {
            gson.fromJson(it, object : TypeToken<List<IgdbGameNetwork>>() {}.type)
        }
    }

    override suspend fun fetchPopularGames(page: Int) = if (page == 1) games() else emptyList()

    override suspend fun fetchComingSoon(now: Long) = games().take(3)

    override suspend fun fetchRecentlyReleased(now: Long) = games().takeLast(3)

    override suspend fun fetchGenres() = DEMO_GENRES

    override suspend fun fetchGamesByGenre(genreId: Int, page: Int) =
        if (page == 1) games().filter { game -> game.genres.orEmpty().any { it.id == genreId } } else emptyList()

    override suspend fun fetchGameDetails(id: Int) = games().firstOrNull { it.id == id }

    override suspend fun searchGames(query: String) =
        games().filter { it.name.orEmpty().contains(query, ignoreCase = true) }

    private companion object {
        const val GAMES_ASSET = "games.json"
        val DEMO_GENRES = listOf(
            IgdbNamedNetwork(12, "Role-playing (RPG)"),
            IgdbNamedNetwork(31, "Adventure"),
            IgdbNamedNetwork(32, "Indie"),
            IgdbNamedNetwork(8, "Platform"),
        )
    }
}
