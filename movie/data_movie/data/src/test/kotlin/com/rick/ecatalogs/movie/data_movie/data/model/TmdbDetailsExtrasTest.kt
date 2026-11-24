package com.rick.ecatalogs.movie.data_movie.data.model

import com.rick.ecatalogs.movie.data_movie.network.model.CastResponse
import com.rick.ecatalogs.movie.data_movie.network.model.ContentRatingResponse
import com.rick.ecatalogs.movie.data_movie.network.model.ContentRatingsResponse
import com.rick.ecatalogs.movie.data_movie.network.model.CreditsResponse
import com.rick.ecatalogs.movie.data_movie.network.model.ReleaseDateResponse
import com.rick.ecatalogs.movie.data_movie.network.model.ReleaseDatesCountryResponse
import com.rick.ecatalogs.movie.data_movie.network.model.ReleaseDatesResponse
import com.rick.ecatalogs.movie.data_movie.network.model.VideoResponse
import com.rick.ecatalogs.movie.data_movie.network.model.VideosResponse
import com.rick.ecatalogs.movie.data_movie.network.model.WatchProviderCountryResponse
import com.rick.ecatalogs.movie.data_movie.network.model.WatchProviderResponse
import com.rick.ecatalogs.movie.data_movie.network.model.WatchProvidersResponse
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TmdbDetailsExtrasTest {

    @Test
    fun bestTrailer_prefersOfficialTrailerOverOthers() {
        val videos = listOf(
            video("teaser", type = "Teaser"),
            video("fan", type = "Trailer", official = false),
            video("official", type = "Trailer", official = true),
        )
        assertEquals("official", videos.bestTrailer()?.key)
    }

    @Test
    fun bestTrailer_fallsBackToTeaser_andIgnoresNonYoutube() {
        val videos = listOf(
            video("vimeo", type = "Trailer", site = "Vimeo"),
            video("teaser", type = "Teaser"),
            video("clip", type = "Clip"),
        )
        assertEquals("teaser", videos.bestTrailer()?.key)
    }

    @Test
    fun bestTrailer_isNullWithoutTrailersOrTeasers() {
        assertNull(listOf(video("clip", type = "Featurette")).bestTrailer())
    }

    @Test
    fun movieAgeRating_prefersTheatricalCertification() {
        val releaseDates = ReleaseDatesResponse(
            results = listOf(
                ReleaseDatesCountryResponse(
                    country = "US",
                    releaseDates = listOf(
                        ReleaseDateResponse(certification = "", type = 1),
                        ReleaseDateResponse(certification = "NR", type = 4),
                        ReleaseDateResponse(certification = "PG-13", type = 3),
                    ),
                ),
                ReleaseDatesCountryResponse(
                    country = "BR",
                    releaseDates = listOf(ReleaseDateResponse(certification = "14", type = 4)),
                ),
                ReleaseDatesCountryResponse(
                    country = "FR",
                    releaseDates = listOf(ReleaseDateResponse(certification = "", type = 3)),
                ),
            ),
        )
        assertEquals(mapOf("US" to "PG-13", "BR" to "14"), releaseDates.asAgeRatings())
    }

    @Test
    fun seriesAgeRating_skipsBlankRatings() {
        val ratings = ContentRatingsResponse(
            results = listOf(
                ContentRatingResponse(country = "US", rating = "TV-MA"),
                ContentRatingResponse(country = "DE", rating = " "),
            ),
        )
        assertEquals(mapOf("US" to "TV-MA"), ratings.asAgeRatings())
    }

    @Test
    fun missingSections_giveEmptyExtras() {
        val extras = tmdbDetailsExtras(
            credits = null,
            videos = null,
            watchProviders = null,
            ageRatings = (null as ReleaseDatesResponse?).asAgeRatings(),
        )
        assertTrue(extras.cast.isEmpty())
        assertNull(extras.trailerYoutubeKey)
        assertTrue(extras.watchProviders.isEmpty())
        assertTrue(extras.ageRatings.isEmpty())
    }

    @Test
    fun cast_isOrderedByBillingAndCapped() {
        val cast = (0 until MAX_CAST + 5).reversed().map {
            CastResponse(id = it, name = "Actor $it", character = null, order = it)
        }
        val extras = tmdbDetailsExtras(
            credits = CreditsResponse(cast),
            videos = VideosResponse(emptyList()),
            watchProviders = null,
            ageRatings = emptyMap(),
        )
        assertEquals(MAX_CAST, extras.cast.size)
        assertEquals("Actor 0", extras.cast.first().name)
        assertEquals("", extras.cast.first().character)
    }

    @Test
    fun watchProviders_areKeptPerCountryInDisplayOrder() {
        val providers = WatchProvidersResponse(
            results = mapOf(
                "US" to WatchProviderCountryResponse(
                    link = "https://www.themoviedb.org/movie/1/watch?locale=US",
                    flatrate = listOf(
                        WatchProviderResponse(2, "Second", "/b.jpg", displayPriority = 5),
                        WatchProviderResponse(1, "First", "/a.jpg", displayPriority = 1),
                    ),
                ),
            ),
        )
        val extras = tmdbDetailsExtras(null, null, providers, emptyMap())

        val us = extras.watchProviders.getValue("US")
        assertEquals(listOf("First", "Second"), us.stream.map { it.name })
        assertTrue(us.rent.isEmpty() && us.buy.isEmpty())
    }

    private fun video(
        key: String,
        type: String,
        site: String = "YouTube",
        official: Boolean? = null,
    ) = VideoResponse(key = key, site = site, type = type, official = official)
}
