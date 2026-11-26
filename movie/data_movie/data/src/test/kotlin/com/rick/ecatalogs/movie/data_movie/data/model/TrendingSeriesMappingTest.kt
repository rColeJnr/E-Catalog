package com.rick.ecatalogs.movie.data_movie.data.model

import com.google.gson.Gson
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingSeriesResponse
import org.junit.Test
import kotlin.test.assertEquals

/** TMDB leaves fields out for little-known series; mapping them mustn't crash. */
class TrendingSeriesMappingTest {

    @Test
    fun seriesMissingFields_mapToEmptyValues() {
        // A search result as TMDB sends it for an obscure show: no date, overview or votes
        val response = Gson().fromJson(
            """{"page": 1, "results": [{"id": 7, "adult": false, "name": "Obscure"}]}""",
            TrendingSeriesResponse::class.java,
        )
        val series = response.results.single()

        val entity = series.asTrendingSeriesEntity()
        assertEquals("Obscure", entity.name)
        assertEquals("", entity.overview)
        assertEquals("", entity.firstAirDate)
        assertEquals(0.0, entity.voteAverage)
        assertEquals(0, entity.voteCount)

        val model = series.asTrendingSeries()
        assertEquals("", model.firstAirDate)
        assertEquals("", model.image)
    }

    @Test
    fun seriesWithoutAName_mapsToAnEmptyName() {
        val response = Gson().fromJson(
            """{"page": 1, "results": [{"id": 8, "adult": false, "name": null, "first_air_date": "2024-01-02"}]}""",
            TrendingSeriesResponse::class.java,
        )

        val entity = response.results.single().asTrendingSeriesEntity()

        assertEquals("", entity.name)
        assertEquals("2024-01-02", entity.firstAirDate)
    }
}
