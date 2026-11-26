package com.rick.ecatalogs.book.screen_book.common

import com.rick.ecatalogs.movie.data_movie.model.tmdb.browse.TmdbTitle
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

/** An adaptation opens the movie or series details deep link declared in the nav graphs. */
@RunWith(RobolectricTestRunner::class)
class TmdbDetailsUriTest {

    @Test
    fun movieAdaptation_opensMovieDetails() {
        val uri = title(isMovie = true).detailsUri()

        assertEquals("com.rick.ecs", uri.scheme)
        assertEquals("trending_movie_details_fragment", uri.host)
        assertEquals(listOf("42"), uri.pathSegments)
    }

    @Test
    fun seriesAdaptation_opensSeriesDetails() {
        val uri = title(isMovie = false).detailsUri()

        assertEquals("com.rick.ecs://trending_series_details_fragment/42", uri.toString())
    }

    private fun title(isMovie: Boolean) =
        TmdbTitle(id = 42, isMovie = isMovie, title = "Frankenstein", posterPath = null, year = 2025)
}
