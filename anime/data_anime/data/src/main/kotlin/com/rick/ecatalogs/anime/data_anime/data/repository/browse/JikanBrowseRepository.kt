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
import com.rick.ecatalogs.anime.data_anime.network.model.hasNextPage
import com.rick.ecatalogs.anime.data_anime.network.model.isStale
import com.rick.ecatalogs.anime.data_anime.network.model.posterUrl
import com.rick.ecatalogs.anime.data_anime.data.model.isAdult
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
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/** Jikan lists beyond the stored top lists, and the richer anime / manga details. */
interface JikanBrowseRepository {
    /** Anime airing this season. Empty if it can't be loaded. */
    fun seasonNow(): Flow<List<JikanItem>>

    /** Anime coming next season. Empty if it can't be loaded. */
    fun seasonUpcoming(): Flow<List<JikanItem>>

    fun recentlyReleasedAnime(): Flow<List<JikanItem>>

    fun recentlyReleasedManga(): Flow<List<JikanItem>>

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
            network.fetchSeasonNow().let { response ->
                response.data.map { it.asItem() }.distinctBy { it.id }.take(SEASON_ROW_SIZE) to response.meta.isStale
            }
        })
    }

    override fun seasonUpcoming(): Flow<List<JikanItem>> = flow {
        emit(cached(CachedList.ANIME_SEASON_UPCOMING, itemListType) {
            network.fetchSeasonUpcoming().let { response ->
                response.data.map { it.asItem() }.distinctBy { it.id }.take(SEASON_ROW_SIZE) to response.meta.isStale
            }
        })
    }

    override fun recentlyReleasedAnime(): Flow<List<JikanItem>> = flow {
        emit(cached(CachedList.ANIME_RECENT, itemListType) {
            network.fetchRecentAnime(recentStartDate()).let { response ->
                response.data
                    .filterNot { it.isAdult() }
                    .map { it.asItem().copy(date = it.startDate, score = it.score?.takeIf { s -> s > 0 }) }
                    .distinctBy { it.id }
                    .take(RECENT_ROW_SIZE) to response.meta.isStale
            }
        })
    }

    override fun recentlyReleasedManga(): Flow<List<JikanItem>> = flow {
        emit(cached(CachedList.MANGA_RECENT, itemListType) {
            network.fetchRecentManga(recentStartDate()).let { response ->
                response.data
                    .filter { it.isComplete() }
                    .map {
                        JikanItem(
                            id = it.id,
                            title = (it.titleEnglish?.takeIf(String::isNotBlank) ?: it.title).orEmpty(),
                            imageUrl = it.posterUrl,
                            date = it.startDate,
                            score = it.score?.takeIf { s -> s > 0 },
                        )
                    }
                    .distinctBy { it.id }
                    .take(RECENT_ROW_SIZE) to response.meta.isStale
            }
        })
    }

    override fun featuredManga(list: MangaList): Flow<List<JikanItem>> = flow {
        emit(cached(CachedList.jikanMangaRow(list.name), itemListType) {
            mangaPage(list, page = 1, genreId = null).let { response ->
                response.data
                    .filter { it.isComplete() }
                    .map { JikanItem(id = it.id, title = (it.titleEnglish?.takeIf(String::isNotBlank) ?: it.title).orEmpty(), imageUrl = it.posterUrl) }
                    .distinctBy { it.id }
                    .take(FEATURED_ROW_SIZE) to response.meta.isStale
            }
        })
    }

    override suspend fun animeGenres(): List<JikanGenre> =
        cached(CachedList.jikanGenres("anime"), genreListType) {
            network.fetchAnimeGenres().let { it.asGenres() to it.meta.isStale }
        }

    override suspend fun mangaGenres(): List<JikanGenre> =
        cached(CachedList.jikanGenres("manga"), genreListType) {
            network.fetchMangaGenres().let { it.asGenres() to it.meta.isStale }
        }

    override fun browseAnime(list: AnimeList, genreId: Int?): Flow<PagingData<Anime>> =
        pager { page ->
            val response = when {
                list == AnimeList.FAVORITES && genreId == null -> network.fetchTopAnime(page, FAVORITE_FILTER)
                else -> {
                    val (orderBy, status) = list.query()
                    network.browseAnime(page, orderBy, "desc", status, genreId)
                }
            }
            response.data.filterNot { it.isAdult() }.map { it.asEntity().asAnime() } to response.meta.hasNextPage
        }

    override fun browseManga(list: MangaList, genreId: Int?): Flow<PagingData<Manga>> =
        pager { page ->
            val response = mangaPage(list, page, genreId)
            response.data.filter { it.isComplete() }
                .map { it.asEntity().asManga() } to response.meta.hasNextPage
        }

    private suspend fun mangaPage(list: MangaList, page: Int, genreId: Int?) = when {
        list == MangaList.FAVORITES && genreId == null -> network.fetchTopManga(page, FAVORITE_FILTER)
        else -> {
            val (orderBy, status) = list.query()
            network.browseManga(page, orderBy, "desc", status, genreId)
        }
    }

    override suspend fun animeExtras(id: Int): AnimeExtras = remembered("extras:$id") {
        val response = network.fetchAnimeFull(id)
        val anime = response.data
        AnimeExtras(
            trailerYoutubeId = anime?.trailer?.youtubeId?.takeIf { it.isNotBlank() }
                ?: anime?.trailer?.embedUrl?.let(::youtubeIdFromEmbedUrl),
            streaming = anime?.streaming.orEmpty().mapNotNull { link ->
                val name = link.name?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val url = link.url?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                StreamingService(name, url)
            },
            broadcast = anime?.broadcast?.let { b ->
                val day = b.day?.takeIf { it.isNotBlank() } ?: return@let null
                val time = b.time?.takeIf { it.isNotBlank() } ?: return@let null
                Broadcast(day, time, japanZoneId(b.timezone))
            },
            relations = anime?.relations.asRelations(),
        ) to response.meta.isStale
    }

    override suspend fun mangaRelations(id: Int): List<JikanRelation> = remembered("manga_rel:$id") {
        network.fetchMangaFull(id).let { it.data?.relations.asRelations() to it.meta.isStale }
    }

    override suspend fun animeCharacters(id: Int): List<AnimeCharacter> = remembered("chars:$id") {
        val response = network.fetchAnimeCharacters(id)
        response.data.orEmpty()
            .mapNotNull { character ->
                val characterId = character.id ?: return@mapNotNull null
                val actor = character.voiceActors.orEmpty()
                    .firstOrNull { it.language.equals("Japanese", ignoreCase = true) }
                AnimeCharacter(
                    id = characterId,
                    name = character.name.orEmpty(),
                    imageUrl = character.imageUrl,
                    isMain = character.role.equals("Main", ignoreCase = true),
                    voiceActor = actor?.id?.let { actorId ->
                        VoiceActor(actorId, actor.name.orEmpty(), actor.imageUrl)
                    },
                )
            }
            .sortedByDescending { it.isMain }
            .take(MAX_CHARACTERS) to response.meta.isStale
    }

    override suspend fun person(id: Int): JikanPerson = remembered("person:$id") {
        val response = network.fetchPerson(id)
        val person = checkNotNull(response.data) { "No person $id" }
        JikanPerson(
            id = person.id,
            name = person.name.orEmpty(),
            nativeName = listOfNotNull(person.familyName, person.givenName)
                .filter { it.isNotBlank() }.joinToString("").ifBlank { null },
            birthday = person.birthDate?.take(10)?.takeIf { it.length == 10 },
            about = person.about.orEmpty().trim(),
            imageUrl = person.images?.medium ?: person.imageUrl ?: person.images?.large,
            roles = person.voices.orEmpty()
                .mapNotNull { voice ->
                    val animeId = voice.animeId ?: return@mapNotNull null
                    VoiceRole(
                        animeId = animeId,
                        animeTitle = voice.animeTitle.orEmpty(),
                        animeImageUrl = voice.animeImageUrl,
                        characterName = voice.characterName.orEmpty(),
                        isMain = voice.characterRole.equals("Main", ignoreCase = true),
                    )
                }
                .sortedByDescending { it.isMain }
                .distinctBy { it.animeId }
                .take(MAX_ROLES),
        ) to response.meta.isStale
    }

    override suspend fun schedule(japanDay: String): List<ScheduledAnime> = remembered("schedule:$japanDay") {
        val response = network.fetchSchedule(japanDay)
        response.data.map { anime ->
            ScheduledAnime(
                id = anime.id,
                title = (anime.titleEnglish?.takeIf { it.isNotBlank() } ?: anime.title).orEmpty(),
                imageUrl = anime.posterUrl,
                broadcast = anime.broadcast?.let { b ->
                    val day = b.day?.takeIf { it.isNotBlank() } ?: return@let null
                    val time = b.time?.takeIf { it.isNotBlank() } ?: return@let null
                    Broadcast(day, time, japanZoneId(b.timezone))
                },
                score = anime.score?.takeIf { it > 0 },
            )
        }.distinctBy { it.id } to response.meta.isStale
    }

    override suspend fun randomAnimeId(): Int? {
        repeat(MAX_RANDOM_TRIES) {
            val anime = network.fetchRandomAnime().data ?: return null
            if (!anime.isAdult()) return anime.id
        }
        return null
    }

    override suspend fun randomMangaId(): Int? = network.fetchRandomManga().data?.id

    override suspend fun animeEpisodes(id: Int): List<AnimeEpisode> = remembered("episodes:$id") {
        val response = network.fetchAnimeEpisodes(id, page = 1)
        response.data.orEmpty().map {
            AnimeEpisode(
                number = it.number,
                title = it.title.orEmpty(),
                airDate = it.aired?.let(::isoDate),
                score = it.score?.takeIf { score -> score > 0 },
                filler = it.filler == true,
                recap = it.recap == true,
            )
        } to response.meta.isStale
    }

    override suspend fun animeRecommendations(id: Int): List<JikanItem> =
        remembered("anime_recs:$id") { network.fetchAnimeRecommendations(id).let { it.asItems() to it.meta.isStale } }

    override suspend fun mangaRecommendations(id: Int): List<JikanItem> =
        remembered("manga_recs:$id") { network.fetchMangaRecommendations(id).let { it.asItems() to it.meta.isStale } }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <T : Any> remembered(key: String, load: suspend () -> Pair<T, Boolean>): T {
        memoryLock.withLock { memory[key] }?.let { return it as T }
        val (value, stale) = load()
        if (!stale) memoryLock.withLock { memory[key] = value }
        return value
    }

    /** Stored copy while fresh; otherwise downloaded and stored. Stale copy or empty on failure. */
    private suspend fun <T> cached(key: String, type: Type, fetch: suspend () -> Pair<List<T>, Boolean>): List<T> =
        withContext(Dispatchers.IO) {
            val file = File(cacheDir, "${key.replace(Regex("[^A-Za-z0-9_-]"), "_")}.json")
            val stored: List<T>? = if (file.exists()) {
                runCatching { gson.fromJson<List<T>>(file.readText(), type) }.getOrNull()
            } else {
                null
            }
            if (stored != null && listCache.isFresh(key)) return@withContext stored
            try {
                val (fetched, stale) = fetch()
                fetched.also {
                    cacheDir.mkdirs()
                    file.writeText(gson.toJson(it, type))
                    if (!stale) listCache.markRefreshed(key)
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

private fun AnimeList.query(): Pair<String, String?> = when (this) {
    AnimeList.POPULAR -> "members" to null
    AnimeList.TOP_RATED -> "score" to null
    AnimeList.AIRING -> "members" to "airing"
    AnimeList.UPCOMING -> "members" to "upcoming"
    AnimeList.FAVORITES -> "members" to null
}

private fun MangaList.query(): Pair<String, String?> = when (this) {
    MangaList.POPULAR -> "members" to null
    MangaList.TOP_RATED -> "score" to null
    MangaList.PUBLISHING -> "members" to "publishing"
    MangaList.FAVORITES -> "members" to null
}

private fun AnimeNetwork.asItem() = JikanItem(
    id = id,
    title = (titleEnglish?.takeIf { it.isNotBlank() } ?: title).orEmpty(),
    imageUrl = posterUrl,
)

private fun JikanGenresResponse.asGenres() = data.orEmpty()
    .mapNotNull { g -> g.name?.takeIf { it.isNotBlank() }?.let { JikanGenre(g.id, it) } }
    .sortedBy { it.name }

private fun JikanRecommendationsResponse.asItems(): List<JikanItem> = data.orEmpty()
    .sortedByDescending { it.votes ?: 0 }
    .map { rec -> JikanItem(id = rec.id, title = rec.title.orEmpty(), imageUrl = rec.imageUrl) }
    .distinctBy { it.id }
    .take(MAX_RECOMMENDATIONS)

/** Browse results reach further into the catalog than the top list; skip incomplete entries. */
private fun MangaNetwork.isComplete() = !title.isNullOrBlank() && !type.isNullOrBlank()

/** "https://www.youtube-nocookie.com/embed/ID?enablejsapi=1…" -> "ID". */
internal fun youtubeIdFromEmbedUrl(url: String): String? =
    Regex("/embed/([A-Za-z0-9_-]{6,})").find(url)?.groupValues?.get(1)

/** Related titles the app can open (anime and manga only). */
private fun List<JikanRelationNetwork>?.asRelations(): List<JikanRelation> = orEmpty()
    .mapNotNull { entry ->
        val relation = entry.relation?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
        val id = entry.id ?: return@mapNotNull null
        val isAnime = when (entry.type?.lowercase()) {
            "anime" -> true
            "manga" -> false
            else -> return@mapNotNull null
        }
        JikanRelation(relation, id, isAnime, entry.title.orEmpty())
    }
    // Adaptations first: the link between an anime and its manga
    .sortedBy { if (it.relation.equals("Adaptation", ignoreCase = true)) 0 else 1 }

internal fun japanZoneId(timezone: String?): String =
    timezone?.takeIf { it.isNotBlank() }
        ?.let { runCatching { ZoneId.of(it.trim(), ZoneId.SHORT_IDS).id }.getOrNull() }
        ?: JAPAN_ZONE

internal fun isoDate(date: String): String? =
    runCatching { LocalDate.parse(date.trim(), MAL_DATE).toString() }.getOrNull()
        ?: date.take(10).takeIf { it.length == 10 && runCatching { LocalDate.parse(it) }.isSuccess }

private val MAL_DATE = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)

private const val JIKAN_PAGE_SIZE = 50
private const val SEASON_ROW_SIZE = 20
private const val RECENT_ROW_SIZE = 30
private const val RECENT_DAYS = 60L

private fun recentStartDate(): String = LocalDate.now().minusDays(RECENT_DAYS).toString()
private const val FEATURED_ROW_SIZE = 25
private const val FAVORITE_FILTER = "favorite"
private const val MAX_RANDOM_TRIES = 3
private const val MAX_CHARACTERS = 25
private const val MAX_ROLES = 60
private const val MAX_RECOMMENDATIONS = 15
private const val JAPAN_ZONE = "Asia/Tokyo"
