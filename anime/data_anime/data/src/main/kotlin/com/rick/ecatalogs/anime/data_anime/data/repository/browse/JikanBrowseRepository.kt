package com.rick.ecatalogs.anime.data_anime.data.repository.browse

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rick.ecatalogs.anime.data_anime.data.model.asEntity
import com.rick.ecatalogs.anime.data_anime.database.model.asAnime
import com.rick.ecatalogs.anime.data_anime.database.model.asManga
import com.rick.ecatalogs.anime.data_anime.model.browse.AnimeEpisode
import com.rick.ecatalogs.anime.data_anime.model.browse.AnimeExtras
import com.rick.ecatalogs.anime.data_anime.model.browse.AnimeList
import com.rick.ecatalogs.anime.data_anime.model.browse.Broadcast
import com.rick.ecatalogs.anime.data_anime.model.browse.JikanGenre
import com.rick.ecatalogs.anime.data_anime.model.browse.JikanItem
import com.rick.ecatalogs.anime.data_anime.model.browse.MangaList
import com.rick.ecatalogs.anime.data_anime.model.browse.StreamingService
import com.rick.ecatalogs.anime.data_anime.model.Anime
import com.rick.ecatalogs.anime.data_anime.model.Manga
import com.rick.ecatalogs.anime.data_anime.network.JikanNetworkDataSource
import com.rick.ecatalogs.anime.data_anime.network.model.AnimeNetwork
import com.rick.ecatalogs.anime.data_anime.network.model.JikanGenresResponse
import com.rick.ecatalogs.anime.data_anime.network.model.JikanRecommendationsResponse
import com.rick.ecatalogs.anime.data_anime.network.model.MangaNetwork
import com.rick.ecatalogs.data.datastore.CachedList
import com.rick.ecatalogs.data.datastore.ListCache
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

/** Jikan lists beyond the stored top lists, and the richer anime / manga details. */
interface JikanBrowseRepository {
    /** Anime airing this season. Empty if it can't be loaded. */
    fun seasonNow(): Flow<List<JikanItem>>

    /** Anime coming next season. Empty if it can't be loaded. */
    fun seasonUpcoming(): Flow<List<JikanItem>>

    suspend fun animeGenres(): List<JikanGenre>

    suspend fun mangaGenres(): List<JikanGenre>

    /** A list other than the stored "popular" one, and/or one genre; paged from the network. */
    fun browseAnime(list: AnimeList, genreId: Int?): Flow<PagingData<Anime>>

    fun browseManga(list: MangaList, genreId: Int?): Flow<PagingData<Manga>>

    /** Trailer, streaming services and broadcast time. @throws on network failure. */
    suspend fun animeExtras(id: Int): AnimeExtras

    /** The first 100 episodes. @throws on network failure. */
    suspend fun animeEpisodes(id: Int): List<AnimeEpisode>

    /** "If you liked this": MyAnimeList user recommendations. @throws on network failure. */
    suspend fun animeRecommendations(id: Int): List<JikanItem>

    suspend fun mangaRecommendations(id: Int): List<JikanItem>
}

/**
 * Season rows and genre lists are small and kept as JSON in the cache directory while fresh
 * ([ListCache], the sync interval), so opening the screens doesn't download them again. Details
 * extras, episodes and recommendations are kept in memory for the session. All requests go
 * through the network layer's Jikan rate limit.
 */
@Singleton
internal class JikanBrowseRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val network: JikanNetworkDataSource,
    private val listCache: ListCache,
) : JikanBrowseRepository {

    private val cacheDir = File(context.cacheDir, "jikan_browse")
    private val gson = Gson()
    private val memory = mutableMapOf<String, Any>()
    private val memoryLock = Mutex()

    override fun seasonNow(): Flow<List<JikanItem>> = flow {
        emit(cached(CachedList.ANIME_SEASON_NOW, itemListType) {
            network.fetchSeasonNow().data.map { it.asItem() }.distinctBy { it.id }
        })
    }

    override fun seasonUpcoming(): Flow<List<JikanItem>> = flow {
        emit(cached(CachedList.ANIME_SEASON_UPCOMING, itemListType) {
            network.fetchSeasonUpcoming().data.map { it.asItem() }.distinctBy { it.id }
        })
    }

    override suspend fun animeGenres(): List<JikanGenre> =
        cached(CachedList.jikanGenres("anime"), genreListType) { network.fetchAnimeGenres().asGenres() }

    override suspend fun mangaGenres(): List<JikanGenre> =
        cached(CachedList.jikanGenres("manga"), genreListType) { network.fetchMangaGenres().asGenres() }

    override fun browseAnime(list: AnimeList, genreId: Int?): Flow<PagingData<Anime>> {
        val (orderBy, sort, status) = when (list) {
            AnimeList.POPULAR -> Triple("members", "desc", null)
            AnimeList.TOP_RATED -> Triple("score", "desc", null)
            AnimeList.AIRING -> Triple("members", "desc", "airing")
            AnimeList.UPCOMING -> Triple("members", "desc", "upcoming")
            AnimeList.FAVORITES -> Triple("favorites", "desc", null)
        }
        return pager { page ->
            val response = network.browseAnime(page, orderBy, sort, status, genreId)
            response.data.map { it.asEntity().asAnime() } to response.pagination.hasNextPage
        }
    }

    override fun browseManga(list: MangaList, genreId: Int?): Flow<PagingData<Manga>> {
        val (orderBy, sort, status) = when (list) {
            MangaList.POPULAR -> Triple("members", "desc", null)
            MangaList.TOP_RATED -> Triple("score", "desc", null)
            MangaList.PUBLISHING -> Triple("members", "desc", "publishing")
            MangaList.FAVORITES -> Triple("favorites", "desc", null)
        }
        return pager { page ->
            val response = network.browseManga(page, orderBy, sort, status, genreId)
            response.data.filter { it.isComplete() }
                .map { it.asEntity().asManga() } to response.pagination.hasNextPage
        }
    }

    override suspend fun animeExtras(id: Int): AnimeExtras = remembered("extras:$id") {
        val anime = network.fetchAnimeFull(id).data
        AnimeExtras(
            trailerYoutubeId = anime?.trailer?.embed_url?.let(::youtubeIdFromEmbedUrl),
            streaming = anime?.streaming.orEmpty().mapNotNull { link ->
                val name = link.name?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val url = link.url?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                StreamingService(name, url)
            },
            broadcast = anime?.broadcast?.let { b ->
                val day = b.day?.takeIf { it.isNotBlank() } ?: return@let null
                val time = b.time?.takeIf { it.isNotBlank() } ?: return@let null
                Broadcast(day, time, b.timezone?.takeIf { it.isNotBlank() } ?: JAPAN_ZONE)
            },
        )
    }

    override suspend fun animeEpisodes(id: Int): List<AnimeEpisode> = remembered("episodes:$id") {
        network.fetchAnimeEpisodes(id, page = 1).data.orEmpty().map {
            AnimeEpisode(
                number = it.number,
                title = it.title.orEmpty(),
                airDate = it.aired?.take(10)?.takeIf { date -> date.length == 10 },
                score = it.score?.takeIf { score -> score > 0 },
                filler = it.filler == true,
                recap = it.recap == true,
            )
        }
    }

    override suspend fun animeRecommendations(id: Int): List<JikanItem> =
        remembered("anime_recs:$id") { network.fetchAnimeRecommendations(id).asItems() }

    override suspend fun mangaRecommendations(id: Int): List<JikanItem> =
        remembered("manga_recs:$id") { network.fetchMangaRecommendations(id).asItems() }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <T : Any> remembered(key: String, load: suspend () -> T): T {
        memoryLock.withLock { memory[key] }?.let { return it as T }
        val value = load()
        memoryLock.withLock { memory[key] = value }
        return value
    }

    /** Stored copy while fresh; otherwise downloaded and stored. Stale copy or empty on failure. */
    private suspend fun <T> cached(key: String, type: Type, fetch: suspend () -> List<T>): List<T> =
        withContext(Dispatchers.IO) {
            val file = File(cacheDir, "${key.replace(Regex("[^A-Za-z0-9_-]"), "_")}.json")
            val stored: List<T>? = if (file.exists()) {
                runCatching { gson.fromJson<List<T>>(file.readText(), type) }.getOrNull()
            } else {
                null
            }
            if (stored != null && listCache.isFresh(key)) return@withContext stored
            try {
                fetch().also {
                    cacheDir.mkdirs()
                    file.writeText(gson.toJson(it, type))
                    listCache.markRefreshed(key)
                }
            } catch (e: Exception) {
                stored ?: emptyList()
            }
        }

    private fun <T : Any> pager(load: suspend (page: Int) -> Pair<List<T>, Boolean>) =
        Pager(PagingConfig(pageSize = JIKAN_PAGE_SIZE, enablePlaceholders = false)) {
            JikanPagingSource(load)
        }.flow

    private companion object {
        val itemListType: Type = object : TypeToken<List<JikanItem>>() {}.type
        val genreListType: Type = object : TypeToken<List<JikanGenre>>() {}.type
    }
}

private class JikanPagingSource<T : Any>(
    private val load: suspend (page: Int) -> Pair<List<T>, Boolean>,
) : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 1
        return try {
            val (items, hasNext) = load(page)
            LoadResult.Page(
                data = items,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (hasNext) page + 1 else null,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? =
        state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey?.plus(1) }
}

private fun AnimeNetwork.asItem() = JikanItem(
    id = id,
    title = (titleEnglish?.takeIf { it.isNotBlank() } ?: title).orEmpty(),
    imageUrl = images?.jpg?.image_url,
)

private fun JikanGenresResponse.asGenres() = data.orEmpty()
    .mapNotNull { g -> g.name?.takeIf { it.isNotBlank() }?.let { JikanGenre(g.id, it) } }
    .sortedBy { it.name }

private fun JikanRecommendationsResponse.asItems(): List<JikanItem> = data.orEmpty()
    .sortedByDescending { it.votes ?: 0 }
    .mapNotNull { rec ->
        val entry = rec.entry ?: return@mapNotNull null
        JikanItem(
            id = entry.id,
            title = entry.title.orEmpty(),
            imageUrl = entry.images?.jpg?.largeImageUrl ?: entry.images?.jpg?.imageUrl,
        )
    }
    .distinctBy { it.id }
    .take(MAX_RECOMMENDATIONS)

/** Browse results reach further into the catalog than the top list; skip incomplete entries. */
@Suppress("SENSELESS_COMPARISON")
private fun MangaNetwork.isComplete() =
    (title as String?) != null && (type as String?) != null && (url as String?) != null &&
        (published as Any?) != null

/** "https://www.youtube-nocookie.com/embed/ID?enablejsapi=1…" -> "ID". */
internal fun youtubeIdFromEmbedUrl(url: String): String? =
    Regex("/embed/([A-Za-z0-9_-]{6,})").find(url)?.groupValues?.get(1)

private const val JIKAN_PAGE_SIZE = 25
private const val MAX_RECOMMENDATIONS = 15
private const val JAPAN_ZONE = "Asia/Tokyo"
