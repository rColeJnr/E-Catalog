package com.rick.ecatalogs.anime.data_anime.network.demo

import com.rick.ecatalogs.anime.data_anime.network.model.AnimeFullResponse
import com.rick.ecatalogs.anime.data_anime.network.model.JikanEpisodesResponse
import com.rick.ecatalogs.anime.data_anime.network.model.JikanGenreNetwork
import com.rick.ecatalogs.anime.data_anime.network.model.JikanGenresResponse
import com.rick.ecatalogs.anime.data_anime.network.model.JikanRecommendationsResponse
import com.rick.ecatalogs.anime.data_anime.network.model.MangaFullResponse
import JvmUnitTestAnimeDemoAssetManager
import com.rick.ecatalogs.anime.data_anime.network.JikanNetworkDataSource
import com.rick.ecatalogs.anime.data_anime.network.model.AnimeResponse
import com.rick.ecatalogs.anime.data_anime.network.model.MangaResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

class DemoJikanNetworkDataSource @Inject constructor(
    private val networkJson: Json,
    private val assets: DemoJikanAssetManager = JvmUnitTestAnimeDemoAssetManager,
) : JikanNetworkDataSource {

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun fetchTopAnime(page: Int, filter: String): AnimeResponse {
        return withContext(IO) {
            assets.open(ANIME_ASSET).use(networkJson::decodeFromStream)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun fetchTopManga(page: Int, filter: String): MangaResponse {
        return withContext(IO) {
            assets.open(MANGA_ASSET).use(networkJson::decodeFromStream)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun searchAnime(query: String, sfw: Boolean, limit: Int): AnimeResponse {
        return withContext(IO) {
            assets.open(SEARCH_ANIME_ASSET).use(networkJson::decodeFromStream)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun searchManga(query: String, sfw: Boolean, limit: Int): MangaResponse {
        return withContext(IO) {
            assets.open(SEARCH_MANGA_ASSET).use(networkJson::decodeFromStream)
        }
    }

    // Browsing and richer details reuse the bundled lists in the demo build
    override suspend fun fetchSeasonNow() = fetchTopAnime(page = 1, filter = "")

    override suspend fun fetchSeasonUpcoming() = fetchTopAnime(page = 1, filter = "")

    override suspend fun fetchAnimeGenres() = JikanGenresResponse(DEMO_GENRES)

    override suspend fun fetchMangaGenres() = JikanGenresResponse(DEMO_GENRES)

    override suspend fun browseAnime(page: Int, orderBy: String, sort: String, status: String?, genres: Int?) =
        fetchTopAnime(page, "")

    override suspend fun browseManga(page: Int, orderBy: String, sort: String, status: String?, genres: Int?) =
        fetchTopManga(page, "")

    override suspend fun fetchAnimeFull(id: Int) =
        AnimeFullResponse(fetchTopAnime(page = 1, filter = "").data.firstOrNull { it.id == id })

    override suspend fun fetchMangaFull(id: Int) =
        MangaFullResponse(fetchTopManga(page = 1, filter = "").data.firstOrNull { it.id == id })

    override suspend fun fetchAnimeEpisodes(id: Int, page: Int) = JikanEpisodesResponse(emptyList())

    override suspend fun fetchAnimeRecommendations(id: Int) = JikanRecommendationsResponse(emptyList())

    override suspend fun fetchMangaRecommendations(id: Int) = JikanRecommendationsResponse(emptyList())

    companion object {
        private const val MANGA_ASSET = "manga.json"
        private const val SEARCH_MANGA_ASSET = "searchmanga.json"
        private const val SEARCH_ANIME_ASSET = "searchanime.json"
        private const val ANIME_ASSET = "anime.json"
    }
}

private val DEMO_GENRES = listOf(
    JikanGenreNetwork(1, "Action"), JikanGenreNetwork(4, "Comedy"), JikanGenreNetwork(8, "Drama"),
    JikanGenreNetwork(10, "Fantasy"), JikanGenreNetwork(22, "Romance"),
)
