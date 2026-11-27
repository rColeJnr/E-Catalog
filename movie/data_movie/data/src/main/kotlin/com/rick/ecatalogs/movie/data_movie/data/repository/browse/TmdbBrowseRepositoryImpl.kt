package com.rick.ecatalogs.movie.data_movie.data.repository.browse

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rick.ecatalogs.data.datastore.CachedList
import com.rick.ecatalogs.data.datastore.ListCache
import com.rick.ecatalogs.movie.data_movie.data.model.asTrendingMovie
import com.rick.ecatalogs.movie.data_movie.data.model.asTrendingSeries
import com.rick.ecatalogs.movie.data_movie.model.tmdb.browse.Episode
import com.rick.ecatalogs.movie.data_movie.model.tmdb.browse.SeasonDetails
import com.rick.ecatalogs.movie.data_movie.model.tmdb.browse.TmdbGenre
import com.rick.ecatalogs.movie.data_movie.model.tmdb.browse.TmdbTitle
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_movie.TrendingMovie
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_series.TrendingSeries
import com.rick.ecatalogs.movie.data_movie.network.TmdbNetworkDataSource
import com.rick.ecatalogs.movie.data_movie.network.model.GenresResponse
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
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Browse rows and genre lists are small, so they're kept as JSON files in the cache directory
 * and reused while fresh ([ListCache]: the sync interval, or the same day for "airing today"),
 * so opening the screens doesn't download them again. Discover results are paged straight from
 * the network, only when the user picks a genre.
 */
@Singleton
internal class TmdbBrowseRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val network: TmdbNetworkDataSource,
    private val listCache: ListCache,
) : TmdbBrowseRepository {

    private val cacheDir = File(context.cacheDir, "tmdb_browse")
    private val gson = Gson()
    private val seasons = mutableMapOf<Pair<Int, Int>, SeasonDetails>()
    private val seasonsLock = Mutex()

    /** Title lookups for the cross-API links, for the session. */
    private val linkCache = java.util.concurrent.ConcurrentHashMap<String, List<TmdbTitle>>()

    override fun upcomingMovies(): Flow<List<TrendingMovie>> = flow {
        val today = LocalDate.now().toString()
        val movies = cached(CachedList.UPCOMING_MOVIES, movieListType) {
            network.fetchUpcomingMovies(region()).results.map { it.asTrendingMovie() }
        }
        // TMDB's "upcoming" window also includes the last few weeks; keep only what's ahead
        emit(movies.filter { it.releaseDate >= today }.sortedBy { it.releaseDate })
    }

    override fun nowPlayingMovies(): Flow<List<TrendingMovie>> = flow {
        emit(
            cached(CachedList.NOW_PLAYING_MOVIES, movieListType) {
                network.fetchNowPlayingMovies(region()).results.map { it.asTrendingMovie() }
            }
        )
    }

    override fun recentlyReleasedMovies(): Flow<List<TrendingMovie>> = flow {
        val today = LocalDate.now()
        emit(
            cached(CachedList.RECENT_MOVIES, movieListType, daily = true) {
                network.fetchRecentlyReleasedMovies(
                    from = today.minusDays(RECENT_DAYS).toString(),
                    to = today.toString(),
                    region = region(),
                ).results.map { it.asTrendingMovie() }.filter { it.image.isNotBlank() }
            }
        )
    }

    override fun recentlyReleasedSeries(): Flow<List<TrendingSeries>> = flow {
        val today = LocalDate.now()
        emit(
            cached(CachedList.RECENT_SERIES, seriesListType, daily = true) {
                network.fetchRecentlyReleasedSeries(
                    from = today.minusDays(RECENT_DAYS).toString(),
                    to = today.toString(),
                ).results.map { it.asTrendingSeries() }.filter { !it.image.isNullOrBlank() }
            }
        )
    }

    override fun airingTodaySeries(): Flow<List<TrendingSeries>> = flow {
        emit(
            cached(CachedList.AIRING_TODAY_SERIES, seriesListType, daily = true) {
                network.fetchAiringTodaySeries().results.map { it.asTrendingSeries() }
            }
        )
    }

    override suspend fun movieGenres(): List<TmdbGenre> {
        val language = Locale.getDefault().toLanguageTag()
        return cached(CachedList.tmdbGenres("movie", language), genreListType) {
            network.fetchMovieGenres(language).asGenres()
        }
    }

    override suspend fun seriesGenres(): List<TmdbGenre> {
        val language = Locale.getDefault().toLanguageTag()
        return cached(CachedList.tmdbGenres("tv", language), genreListType) {
            network.fetchSeriesGenres(language).asGenres()
        }
    }

    override fun discoverMovies(genreId: Int): Flow<PagingData<TrendingMovie>> =
        Pager(PagingConfig(pageSize = TMDB_PAGE_SIZE, enablePlaceholders = false)) {
            NetworkPagingSource { page ->
                network.discoverMovies(genreId, page).results.map { it.asTrendingMovie() }
            }
        }.flow

    override fun discoverSeries(genreId: Int): Flow<PagingData<TrendingSeries>> =
        Pager(PagingConfig(pageSize = TMDB_PAGE_SIZE, enablePlaceholders = false)) {
            NetworkPagingSource { page ->
                network.discoverSeries(genreId, page).results.map { it.asTrendingSeries() }
            }
        }.flow

    override suspend fun season(seriesId: Int, seasonNumber: Int): SeasonDetails {
        val key = seriesId to seasonNumber
        seasonsLock.withLock { seasons[key] }?.let { return it }
        val response = network.fetchSeason(seriesId, seasonNumber)
        val details = SeasonDetails(
            number = response.seasonNumber,
            name = response.name.orEmpty(),
            overview = response.overview.orEmpty().trim(),
            episodes = response.episodes.orEmpty().map {
                Episode(
                    id = it.id,
                    number = it.episodeNumber,
                    name = it.name.orEmpty(),
                    overview = it.overview.orEmpty().trim(),
                    airDate = it.airDate?.takeIf { date -> date.isNotBlank() },
                    stillPath = it.stillPath?.takeIf { path -> path.isNotBlank() },
                    runtime = it.runtime?.takeIf { minutes -> minutes > 0 },
                    voteAverage = it.voteAverage ?: 0.0,
                )
            },
        )
        seasonsLock.withLock { seasons[key] = details }
        return details
    }

    override suspend fun findFilm(title: String, year: Int?): TmdbTitle? {
        val key = normalizedTitle(title)
        if (key.isEmpty()) return null
        return linkCache.getOrPut("film:$key:$year") {
            val matches = network.searchMovie(query = title).results
                .map { it.asTmdbTitle() }
                .filter { normalizedTitle(it.title) == key }
            val nearYear = year?.let { y -> matches.filter { it.year != null && it.year in (y - 2)..y } }
            listOfNotNull((nearYear?.firstOrNull() ?: matches.firstOrNull()))
        }.firstOrNull()
    }

    override suspend fun screenAdaptations(bookTitle: String): List<TmdbTitle> {
        // "Frankenstein; Or, The Modern Prometheus" -> "Frankenstein"
        val title = bookTitle.split(';', ':', '(').first().trim()
        val key = normalizedTitle(title)
        if (key.length < MIN_TITLE_LENGTH) return emptyList()
        return linkCache.getOrPut("adaptations:$key") {
            val movies = runCatching { network.searchMovie(query = title).results }
                .getOrDefault(emptyList()).map { it.asTmdbTitle() to (it.voteCount ?: 0) }
            val series = runCatching { network.searchSeries(query = title).results }
                .getOrDefault(emptyList()).map { it.asTmdbTitle() to (it.voteCount ?: 0) }
            (movies + series)
                .filter { (it, _) -> normalizedTitle(it.title) == key }
                .sortedByDescending { (_, votes) -> votes }
                .map { it.first }
                .take(MAX_ADAPTATIONS)
        }
    }

    /**
     * The stored copy of [key] while it's fresh; otherwise downloads and stores it. If the
     * download fails, a stale copy is better than nothing; with no copy at all, an empty list.
     */
    private suspend fun <T> cached(
        key: String,
        type: Type,
        daily: Boolean = false,
        fetch: suspend () -> List<T>,
    ): List<T> = withContext(Dispatchers.IO) {
        val file = File(cacheDir, "${key.replace(Regex("[^A-Za-z0-9_-]"), "_")}.json")
        val fresh = if (daily) listCache.isFreshToday(key) else listCache.isFresh(key)
        val stored: List<T>? = read(file, type)
        if (fresh && stored != null) return@withContext stored
        try {
            fetch().also { items ->
                cacheDir.mkdirs()
                file.writeText(gson.toJson(items, type))
                listCache.markRefreshed(key)
            }
        } catch (e: Exception) {
            stored ?: emptyList()
        }
    }

    private fun <T> read(file: File, type: Type): List<T>? =
        if (!file.exists()) null
        else runCatching { gson.fromJson<List<T>>(file.readText(), type) }.getOrNull()

    private fun region(): String? = Locale.getDefault().country.uppercase().ifBlank { null }

    private fun GenresResponse.asGenres(): List<TmdbGenre> = genres.orEmpty()
        .mapNotNull { genre -> genre.name?.takeIf { it.isNotBlank() }?.let { TmdbGenre(genre.id, it) } }

    private companion object {
        val movieListType: Type = object : TypeToken<List<TrendingMovie>>() {}.type
        val seriesListType: Type = object : TypeToken<List<TrendingSeries>>() {}.type
        val genreListType: Type = object : TypeToken<List<TmdbGenre>>() {}.type
    }
}

/** Pages a TMDB list straight from the network (page 1, 2, …; TMDB stops at page 500). */
private class NetworkPagingSource<T : Any>(
    private val fetch: suspend (page: Int) -> List<T>,
) : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 1
        return try {
            val items = fetch(page)
            LoadResult.Page(
                data = items,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (items.isEmpty() || page >= TMDB_MAX_PAGE) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? =
        state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey?.plus(1) }
}

/**
 * Lowercase letters and digits only, so "The Let Them Theory" == "THE LET THEM THEORY" and
 * "Amélie" == "Amelie". Accents are dropped from Latin letters only: in other scripts they make
 * a different letter (Russian "й" is not "и").
 */
internal fun normalizedTitle(title: String): String =
    java.text.Normalizer.normalize(
        java.text.Normalizer.normalize(title, java.text.Normalizer.Form.NFD)
            .replace(Regex("(?<=\\p{IsLatin})\\p{M}+"), ""),
        java.text.Normalizer.Form.NFC,
    )
        .lowercase()
        .replace(Regex("[^\\p{L}\\p{N}]+"), "")

private fun com.rick.ecatalogs.movie.data_movie.network.model.TrendingMovieNetwork.asTmdbTitle() = TmdbTitle(
    id = id,
    isMovie = true,
    title = title.orEmpty(),
    posterPath = image?.takeIf { it.isNotBlank() },
    year = releaseDate?.take(4)?.toIntOrNull(),
)

private fun com.rick.ecatalogs.movie.data_movie.network.model.TrendingSeriesNetwork.asTmdbTitle() = TmdbTitle(
    id = id,
    isMovie = false,
    title = name.orEmpty(),
    posterPath = image?.takeIf { it.isNotBlank() },
    year = firstAirDate?.take(4)?.toIntOrNull(),
)

private const val TMDB_PAGE_SIZE = 20
private const val RECENT_DAYS = 60L
private const val MAX_ADAPTATIONS = 10
private const val MIN_TITLE_LENGTH = 3
private const val TMDB_MAX_PAGE = 500
