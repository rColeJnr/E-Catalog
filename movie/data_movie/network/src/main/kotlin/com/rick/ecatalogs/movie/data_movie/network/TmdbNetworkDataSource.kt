package com.rick.ecatalogs.movie.data_movie.network

import com.rick.ecatalogs.movie.data_movie.network.model.GenresResponse
import com.rick.ecatalogs.movie.data_movie.network.model.MovieResponse
import com.rick.ecatalogs.movie.data_movie.network.model.SeasonResponse
import com.rick.ecatalogs.movie.data_movie.network.model.PersonResponse
import com.rick.ecatalogs.movie.data_movie.network.model.SeriesResponse
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingMovieResponse
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingSeriesResponse

interface TmdbNetworkDataSource {


    suspend fun fetchTrendingMovies(
        page: Int,
        language: String = "en-US",
    ): TrendingMovieResponse

    suspend fun fetchTrendingSeries(
        page: Int,
        language: String = "en-US",
    ): TrendingSeriesResponse

    suspend fun fetchSeries(
        id: Int,
        appendResponse: String = SERIES_APPEND_TO_RESPONSE,
        language: String = "en-US",
    ): SeriesResponse

    suspend fun fetchMovie(
        id: Int,
        appendResponse: String = MOVIE_APPEND_TO_RESPONSE,
        language: String = "en-US",
    ): MovieResponse

    /** Movies opening soon (TMDB's upcoming window), for [region] (ISO 3166-1) when given. */
    suspend fun fetchUpcomingMovies(region: String?): TrendingMovieResponse

    /** Movies in theaters now, for [region] when given. */
    suspend fun fetchNowPlayingMovies(region: String?): TrendingMovieResponse

    /** Series with an episode airing today. */
    suspend fun fetchAiringTodaySeries(): TrendingSeriesResponse

    suspend fun fetchRecentlyReleasedMovies(from: String, to: String, region: String?): TrendingMovieResponse

    suspend fun fetchRecentlyReleasedSeries(from: String, to: String): TrendingSeriesResponse

    /** Most popular movies of a genre (TMDB discover). */
    suspend fun discoverMovies(genreId: Int, page: Int): TrendingMovieResponse

    /** Most popular series of a genre (TMDB discover). */
    suspend fun discoverSeries(genreId: Int, page: Int): TrendingSeriesResponse

    /** Movie genres with names in [language] (an IETF tag, e.g. "pt-BR"). */
    suspend fun fetchMovieGenres(language: String): GenresResponse

    /** Series genres with names in [language]. */
    suspend fun fetchSeriesGenres(language: String): GenresResponse

    /** One season of a series, with its episodes. */
    suspend fun fetchSeason(seriesId: Int, seasonNumber: Int): SeasonResponse

    /** An actor: biography plus every movie and series they appeared in, in one request. */
    suspend fun fetchPerson(
        id: Int,
        appendResponse: String = PERSON_APPEND_TO_RESPONSE,
        language: String = "en-US",
    ): PersonResponse

    suspend fun searchSeries(
        query: String,
        include_adult: Boolean = false,
        language: String = "en-US",
    ): TrendingSeriesResponse

    suspend fun searchMovie(
        query: String,
        include_adult: Boolean = false,
        language: String = "en-US",
    ): TrendingMovieResponse
}

/**
 * Sections bundled into the single details request (TMDB `append_to_response`): similar titles,
 * cast, videos (trailer), images (backdrops), where to watch, and the age rating.
 */
const val MOVIE_APPEND_TO_RESPONSE = "similar,credits,videos,images,watch/providers,release_dates"
const val SERIES_APPEND_TO_RESPONSE = "similar,credits,videos,images,watch/providers,content_ratings"
const val PERSON_APPEND_TO_RESPONSE = "combined_credits"

/** With `images`: English and textless images (the defaults would follow `language` only). */
const val IMAGE_LANGUAGES = "en,null"
