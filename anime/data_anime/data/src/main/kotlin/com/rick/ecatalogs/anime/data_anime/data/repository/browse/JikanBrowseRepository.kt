package com.rick.ecatalogs.anime.data_anime.data.repository.browse

import com.rick.ecatalogs.anime.data_anime.model.browse.AnimeCharacter
import com.rick.ecatalogs.anime.data_anime.model.browse.JikanPerson
import com.rick.ecatalogs.anime.data_anime.model.browse.JikanRelation
import com.rick.ecatalogs.anime.data_anime.model.browse.ScheduledAnime
import com.rick.ecatalogs.anime.data_anime.model.browse.VoiceActor
import com.rick.ecatalogs.anime.data_anime.model.browse.VoiceRole
import com.rick.ecatalogs.anime.data_anime.network.model.JikanRelationNetwork
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

    /** The first page of a manga list, for a featured row on the manga screen. */
    fun featuredManga(list: MangaList): Flow<List<JikanItem>>

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

    /** Adaptations, sequels… of a manga. @throws on network failure. */
    suspend fun mangaRelations(id: Int): List<JikanRelation>

    /** Characters (main first) with their Japanese voice actors. @throws on network failure. */
    suspend fun animeCharacters(id: Int): List<AnimeCharacter>

    /** A voice actor and their roles. @throws on network failure. */
    suspend fun person(id: Int): JikanPerson

    /**
     * Anime broadcast on a weekday in Japan ([japanDay] "monday"…"sunday"), up to 75.
     * @throws on network failure.
     */
    suspend fun schedule(japanDay: String): List<ScheduledAnime>

    /** A random (safe for work) anime / manga id, or null. @throws on network failure. */
    suspend fun randomAnimeId(): Int?

    suspend fun randomMangaId(): Int?
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

    override fun featuredManga(list: MangaList): Flow<List<JikanItem>> = flow {
        val (orderBy, status) = list.query()
        emit(cached(CachedList.jikanMangaRow(list.name), itemListType) {
            network.browseManga(1, orderBy, "desc", status, null).data
                .filter { it.isComplete() }
                .map { JikanItem(id = it.id, title = (it.titleEnglish?.takeIf(String::isNotBlank) ?: it.title), imageUrl = it.images?.jpg?.image_url) }
                .distinctBy { it.id }
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
        val (orderBy, status) = list.query()
        return pager { page ->
            val response = network.browseManga(page, orderBy, "desc", status, genreId)
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
            relations = anime?.relations.asRelations(),
        )
    }

    override suspend fun mangaRelations(id: Int): List<JikanRelation> = remembered("manga_rel:$id") {
        network.fetchMangaFull(id).data?.relations.asRelations()
    }

    override suspend fun animeCharacters(id: Int): List<AnimeCharacter> = remembered("chars:$id") {
        network.fetchAnimeCharacters(id).data.orEmpty()
            .mapNotNull { role ->
                val character = role.character ?: return@mapNotNull null
                val actor = role.voiceActors.orEmpty()
                    .firstOrNull { it.language.equals("Japanese", ignoreCase = true) }
                    ?.person
                AnimeCharacter(
                    id = character.id,
                    name = character.name.orEmpty(),
                    imageUrl = character.images?.jpg?.imageUrl,
                    isMain = role.role.equals("Main", ignoreCase = true),
                    voiceActor = actor?.let {
                        VoiceActor(it.id, it.name.orEmpty(), it.images?.jpg?.imageUrl)
                    },
                )
            }
            .sortedByDescending { it.isMain }
            .take(MAX_CHARACTERS)
    }

    override suspend fun person(id: Int): JikanPerson = remembered("person:$id") {
        val person = checkNotNull(network.fetchPerson(id).data) { "No person $id" }
        JikanPerson(
            id = person.id,
            name = person.name.orEmpty(),
            nativeName = listOfNotNull(person.familyName, person.givenName)
                .filter { it.isNotBlank() }.joinToString("").ifBlank { null },
            birthday = person.birthday?.take(10)?.takeIf { it.length == 10 },
            about = person.about.orEmpty().trim(),
            imageUrl = person.images?.jpg?.imageUrl,
            roles = person.voices.orEmpty()
                .mapNotNull { voice ->
                    val anime = voice.anime ?: return@mapNotNull null
                    VoiceRole(
                        animeId = anime.id,
                        animeTitle = anime.title.orEmpty(),
                        animeImageUrl = anime.images?.jpg?.largeImageUrl ?: anime.images?.jpg?.imageUrl,
                        characterName = voice.character?.name.orEmpty(),
                        isMain = voice.role.equals("Main", ignoreCase = true),
                    )
                }
                .sortedByDescending { it.isMain }
                .distinctBy { it.animeId }
                .take(MAX_ROLES),
        )
    }

    override suspend fun schedule(japanDay: String): List<ScheduledAnime> = remembered("schedule:$japanDay") {
        val shows = mutableListOf<ScheduledAnime>()
        var page = 1
        while (page <= MAX_SCHEDULE_PAGES) {
            val response = network.fetchSchedule(japanDay, page)
            shows += response.data.map { anime ->
                ScheduledAnime(
                    id = anime.id,
                    title = (anime.titleEnglish?.takeIf { it.isNotBlank() } ?: anime.title).orEmpty(),
                    imageUrl = anime.images?.jpg?.image_url,
                    broadcast = anime.broadcast?.let { b ->
                        val day = b.day?.takeIf { it.isNotBlank() } ?: return@let null
                        val time = b.time?.takeIf { it.isNotBlank() } ?: return@let null
                        Broadcast(day, time, b.timezone?.takeIf { it.isNotBlank() } ?: JAPAN_ZONE)
                    },
                    score = anime.score?.takeIf { it > 0 },
                )
            }
            if (!response.pagination.hasNextPage) break
            page++
        }
        shows.distinctBy { it.id }
    }

    // Not remembered: a new pick each time
    override suspend fun randomAnimeId(): Int? = network.fetchRandomAnime().data?.id

    override suspend fun randomMangaId(): Int? = network.fetchRandomManga().data?.id

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

/** Jikan's order_by and status for a manga list (always sorted descending). */
private fun MangaList.query(): Pair<String, String?> = when (this) {
    MangaList.POPULAR -> "members" to null
    MangaList.TOP_RATED -> "score" to null
    MangaList.PUBLISHING -> "members" to "publishing"
    MangaList.FAVORITES -> "favorites" to null
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

/** Related titles the app can open (anime and manga only). */
private fun List<JikanRelationNetwork>?.asRelations(): List<JikanRelation> = orEmpty()
    .flatMap { group ->
        val relation = group.relation?.takeIf { it.isNotBlank() } ?: return@flatMap emptyList()
        group.entry.orEmpty().mapNotNull { entry ->
            val isAnime = when (entry.type?.lowercase()) {
                "anime" -> true
                "manga" -> false
                else -> return@mapNotNull null
            }
            JikanRelation(relation, entry.id, isAnime, entry.name.orEmpty())
        }
    }
    // Adaptations first: the link between an anime and its manga
    .sortedBy { if (it.relation.equals("Adaptation", ignoreCase = true)) 0 else 1 }

private const val JIKAN_PAGE_SIZE = 25
private const val MAX_CHARACTERS = 25
private const val MAX_ROLES = 60
private const val MAX_SCHEDULE_PAGES = 3
private const val MAX_RECOMMENDATIONS = 15
private const val JAPAN_ZONE = "Asia/Tokyo"
