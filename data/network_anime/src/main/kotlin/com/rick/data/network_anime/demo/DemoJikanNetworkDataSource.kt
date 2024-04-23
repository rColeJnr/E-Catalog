package com.rick.data.network_anime.demo

import JvmUnitTestAnimeDemoAssetManager
import com.rick.data.network_anime.JikanNetworkDataSource
import com.rick.data.network_anime.model.AnimeResponse
import com.rick.data.network_anime.model.MangaResponse
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
    override suspend fun searchAnime(query: String, sfw: Boolean, max_score: Int): AnimeResponse {
        return withContext(IO) {
            assets.open(SEARCH_ANIME_ASSET).use(networkJson::decodeFromStream)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun searchManga(query: String, sfw: Boolean, max_score: Int): MangaResponse {
        return withContext(IO) {
            assets.open(SEARCH_MANGA_ASSET).use(networkJson::decodeFromStream)
        }
    }

    companion object {
        private const val MANGA_ASSET = "manga.json"
        private const val SEARCH_MANGA_ASSET = "search_manga.json"
        private const val SEARCH_ANIME_ASSET = "search_anime.json"
        private const val ANIME_ASSET = "anime.json"
    }
}