package com.rick.ecatalogs.movie.data_movie.network.demo

import JvmUnitTestMovieDemoAssetManager
import com.rick.ecatalogs.movie.data_movie.network.TmdbNetworkDataSource
import com.rick.ecatalogs.movie.data_movie.network.model.GenreNetwork
import com.rick.ecatalogs.movie.data_movie.network.model.GenresResponse
import com.rick.ecatalogs.movie.data_movie.network.model.MovieResponse
import com.rick.ecatalogs.movie.data_movie.network.model.SeasonResponse
import com.rick.ecatalogs.movie.data_movie.network.model.PersonResponse
import com.rick.ecatalogs.movie.data_movie.network.model.SeriesResponse
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingMovieResponse
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingSeriesResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

class DemoTmdbNetworkDataSource @Inject constructor(
    private val networkJson: Json,
    private val assets: DemoMovieAssetManager = JvmUnitTestMovieDemoAssetManager,
) : TmdbNetworkDataSource {
    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun fetchTrendingMovies(
        page: Int,
        language: String,
    ): TrendingMovieResponse =
        withContext(IO) {
            assets.open(TRENDING_MOVIE_ASSET).use(networkJson::decodeFromStream)
        }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun fetchTrendingSeries(
        page: Int,
        language: String,
    ): TrendingSeriesResponse = withContext(IO) {
        assets.open(TRENDING_SERIES_ASSET).use(networkJson::decodeFromStream)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun fetchSeries(
        id: Int,
        appendResponse: String,
        language: String,
    ): SeriesResponse = withContext(IO) {
        assets.open(SERIES_ASSET).use(networkJson::decodeFromStream)
    }

    // Browse rows and genre lists reuse the bundled trending lists in the demo build
    override suspend fun fetchUpcomingMovies(region: String?) = fetchTrendingMovies(page = 1)

    override suspend fun fetchNowPlayingMovies(region: String?) = fetchTrendingMovies(page = 1)

    override suspend fun fetchAiringTodaySeries() = fetchTrendingSeries(page = 1)

    override suspend fun fetchRecentlyReleasedMovies(from: String, to: String, region: String?) =
        fetchTrendingMovies(page = 1)

    override suspend fun fetchRecentlyReleasedSeries(from: String, to: String) = fetchTrendingSeries(page = 1)

    override suspend fun discoverMovies(genreId: Int, page: Int) = fetchTrendingMovies(page)

    override suspend fun discoverSeries(genreId: Int, page: Int) = fetchTrendingSeries(page)

    override suspend fun fetchMovieGenres(language: String) = GenresResponse(DEMO_MOVIE_GENRES)

    override suspend fun fetchSeriesGenres(language: String) = GenresResponse(DEMO_SERIES_GENRES)

    override suspend fun fetchSeason(seriesId: Int, seasonNumber: Int) =
        SeasonResponse(seasonNumber = seasonNumber, episodes = emptyList())

    /** No person asset is bundled; the demo shows a placeholder actor with no credits. */
    override suspend fun fetchPerson(
        id: Int,
        appendResponse: String,
        language: String,
    ): PersonResponse = PersonResponse(
        id = id,
        name = "Demo actor",
        biography = "Actor details are loaded from TMDB in the prod build.",
        knownForDepartment = "Acting",
    )

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun fetchMovie(
        id: Int,
        appendResponse: String,
        language: String,
    ): MovieResponse = withContext(IO) {
        assets.open(MOVIE_ASSET).use(networkJson::decodeFromStream)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun searchSeries(
        query: String,
        include_adult: Boolean,
        language: String,
    ): TrendingSeriesResponse = withContext(IO) {
        assets.open(SEARCH_SERIES_ASSET).use(networkJson::decodeFromStream)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun searchMovie(
        query: String,
        include_adult: Boolean,
        language: String,
    ): TrendingMovieResponse = withContext(IO) {
        assets.open(SEARCH_MOVIE_ASSET).use(networkJson::decodeFromStream)
    }

    companion object {
        private const val TRENDING_MOVIE_ASSET = "trendingmovie.json"
        private const val TRENDING_SERIES_ASSET = "trendingseries.json"
        private const val MOVIE_ASSET = "movie_details.json"
        private const val SERIES_ASSET = "series_details.json"
        private const val SEARCH_SERIES_ASSET = "search_series.json"
        private const val SEARCH_MOVIE_ASSET = "searchMovie.json"
    }
}

private val DEMO_MOVIE_GENRES = listOf(
    GenreNetwork(28, "Action"), GenreNetwork(35, "Comedy"), GenreNetwork(18, "Drama"),
    GenreNetwork(27, "Horror"), GenreNetwork(878, "Science Fiction"),
)
private val DEMO_SERIES_GENRES = listOf(
    GenreNetwork(10759, "Action & Adventure"), GenreNetwork(35, "Comedy"),
    GenreNetwork(18, "Drama"), GenreNetwork(10765, "Sci-Fi & Fantasy"),
)
