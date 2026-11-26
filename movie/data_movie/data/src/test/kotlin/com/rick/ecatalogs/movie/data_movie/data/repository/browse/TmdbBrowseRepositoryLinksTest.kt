package com.rick.ecatalogs.movie.data_movie.data.repository.browse

import com.rick.ecatalogs.movie.data_movie.data.testing.testContext
import com.rick.ecatalogs.movie.data_movie.data.testing.testListCache
import com.rick.ecatalogs.movie.data_movie.data.testing.unimplemented
import com.rick.ecatalogs.movie.data_movie.model.tmdb.browse.TmdbTitle
import com.rick.ecatalogs.movie.data_movie.network.TmdbNetworkDataSource
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingMovieNetwork
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingMovieResponse
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingSeriesNetwork
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingSeriesResponse
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** The cross-API links: a review's film ([findFilm]) and a book's adaptations. */
class TmdbBrowseRepositoryLinksTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private lateinit var network: FakeTmdbNetwork
    private lateinit var subject: TmdbBrowseRepositoryImpl

    @Before
    fun setUp() {
        network = FakeTmdbNetwork()
        subject = TmdbBrowseRepositoryImpl(testContext(tmp.root), network, testListCache())
    }

    @Test
    fun findFilm_prefersTheExactTitleReleasedNearTheReview() = runTest {
        network.movies = listOf(
            movie(1, "Wicked", "1998-05-01"),
            movie(2, "Wicked: For Good", "2025-11-21"),
            movie(3, "WICKED", "2024-11-22"),
        )

        assertEquals(3, subject.findFilm("Wicked", year = 2025)?.id)
    }

    @Test
    fun findFilm_withoutAYearOrANearYear_takesTheFirstExactMatch() = runTest {
        network.movies = listOf(movie(1, "Anora", "2024-10-18"), movie(2, "Anora", "1970-01-01"))

        assertEquals(1, subject.findFilm("Anora", year = null)?.id)
        assertEquals(1, subject.findFilm("Anora", year = 1990)?.id)
    }

    @Test
    fun findFilm_ignoresTitlesThatOnlyContainTheName() = runTest {
        network.movies = listOf(movie(1, "The Brutalist Way", "2024-01-01"))

        assertNull(subject.findFilm("The Brutalist", year = 2024))
    }

    @Test
    fun findFilm_mapsTheMatch() = runTest {
        network.movies = listOf(movie(7, "Amélie", "2001-04-25", poster = "/amelie.jpg"))

        assertEquals(
            TmdbTitle(id = 7, isMovie = true, title = "Amélie", posterPath = "/amelie.jpg", year = 2001),
            subject.findFilm("Amelie", year = 2001),
        )
    }

    @Test
    fun findFilm_withoutLettersOrDigits_doesNotSearch() = runTest {
        assertNull(subject.findFilm("‘’ !", year = 2024))
        assertTrue(network.queries.isEmpty())
    }

    @Test
    fun findFilm_isRememberedForTheSession() = runTest {
        network.movies = listOf(movie(1, "Anora", "2024-10-18"))

        subject.findFilm("Anora", 2024)
        subject.findFilm("ANORA", 2024)

        assertEquals(listOf("movie:Anora"), network.queries)
    }

    @Test
    fun adaptations_searchTheMainTitle_moviesAndSeries_mostVotedFirst() = runTest {
        network.movies = listOf(
            movie(1, "Frankenstein", "1931-11-21", votes = 1_000),
            movie(2, "Young Frankenstein", "1974-12-15", votes = 5_000),
            movie(3, "Frankenstein", "2025-10-17", votes = 3_000),
        )
        network.series = listOf(series(4, "Frankenstein", "2004-10-10", votes = 50))

        val adaptations = subject.screenAdaptations("Frankenstein; Or, The Modern Prometheus")

        assertEquals(listOf(3, 1, 4), adaptations.map { it.id })
        assertEquals(listOf(true, true, false), adaptations.map { it.isMovie })
        assertEquals(listOf("movie:Frankenstein", "series:Frankenstein"), network.queries)
    }

    @Test
    fun adaptations_stillShowMoviesWhenTheSeriesSearchFails() = runTest {
        network.movies = listOf(movie(1, "Dune", "2021-09-15"))
        network.series = null

        assertEquals(listOf(1), subject.screenAdaptations("Dune").map { it.id })
    }

    @Test
    fun adaptations_areCappedAtTen() = runTest {
        network.movies = (1..15).map { movie(it, "Emma", "19${it + 80}-01-01", votes = it) }

        assertEquals((15 downTo 6).toList(), subject.screenAdaptations("Emma").map { it.id })
    }

    @Test
    fun adaptations_skipVeryShortTitles() = runTest {
        // Would match every film called "It", "Us"…
        assertEquals(emptyList(), subject.screenAdaptations("It: A Novel"))
        assertTrue(network.queries.isEmpty())
    }

    private fun movie(id: Int, title: String, released: String, votes: Int = 0, poster: String? = null) =
        TrendingMovieNetwork(
            id = id, adult = false, image = poster, title = title, overview = null, popularity = null,
            releaseDate = released, voteAverage = null, voteCount = votes,
        )

    private fun series(id: Int, name: String, firstAired: String, votes: Int) = TrendingSeriesNetwork(
        adult = false, image = null, id = id, name = name, overview = "", popularity = 0.0,
        firstAirDate = firstAired, voteAverage = 0.0, voteCount = votes,
    )

    /** Search results set by the test; null results are a network failure. */
    private class FakeTmdbNetwork : TmdbNetworkDataSource by unimplemented() {
        val queries = mutableListOf<String>()
        var movies: List<TrendingMovieNetwork>? = emptyList()
        var series: List<TrendingSeriesNetwork>? = emptyList()

        override suspend fun searchMovie(
            query: String,
            include_adult: Boolean,
            language: String,
        ): TrendingMovieResponse {
            queries += "movie:$query"
            return TrendingMovieResponse(1, movies ?: throw IOException("offline"))
        }

        override suspend fun searchSeries(
            query: String,
            include_adult: Boolean,
            language: String,
        ): TrendingSeriesResponse {
            queries += "series:$query"
            return TrendingSeriesResponse(1, series ?: throw IOException("offline"))
        }
    }
}
