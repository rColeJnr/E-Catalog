package com.rick.ecatalogs.movie.data_movie.data.repository.browse

import androidx.paging.PagingData
import com.rick.ecatalogs.movie.data_movie.model.tmdb.browse.SeasonDetails
import com.rick.ecatalogs.movie.data_movie.model.tmdb.browse.TmdbGenre
import com.rick.ecatalogs.movie.data_movie.model.tmdb.browse.TmdbTitle
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_movie.TrendingMovie
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_series.TrendingSeries
import kotlinx.coroutines.flow.Flow

/** TMDB lists beyond trending: browse rows, genres (discover) and series seasons. */
interface TmdbBrowseRepository {
    /** Movies releasing from today on, soonest first. Empty if it can't be loaded. */
    fun upcomingMovies(): Flow<List<TrendingMovie>>

    /** Movies in theaters now. Empty if it can't be loaded. */
    fun nowPlayingMovies(): Flow<List<TrendingMovie>>

    /** Series with an episode airing today. Empty if it can't be loaded. */
    fun airingTodaySeries(): Flow<List<TrendingSeries>>

    /** Movie genres in the user's language. Empty if they can't be loaded. */
    suspend fun movieGenres(): List<TmdbGenre>

    /** Series genres in the user's language. Empty if they can't be loaded. */
    suspend fun seriesGenres(): List<TmdbGenre>

    /** Most popular movies of a genre, paged from the network (not stored). */
    fun discoverMovies(genreId: Int): Flow<PagingData<TrendingMovie>>

    /** Most popular series of a genre, paged from the network (not stored). */
    fun discoverSeries(genreId: Int): Flow<PagingData<TrendingSeries>>

    /** A season with its episodes. @throws on network failure. */
    suspend fun season(seriesId: Int, seasonNumber: Int): SeasonDetails

    /**
     * The film a review is about: an exact title match, preferring a release year close to
     * [year]. Null when nothing matches well enough. @throws on network failure.
     */
    suspend fun findFilm(title: String, year: Int?): TmdbTitle?

    /** Films and series with the same title as a book (its screen adaptations). */
    suspend fun screenAdaptations(bookTitle: String): List<TmdbTitle>
}
