package com.rick.ecatalogs.book.screen_book.common

import com.rick.ecatalogs.data.ui_components.common.EcsPosterItem
import com.rick.ecatalogs.movie.data_movie.model.tmdb.browse.TmdbTitle
import org.junit.Test
import kotlin.test.assertEquals

class AdaptationPosterTest {

    @Test
    fun adaptation_showsItsTmdbPosterAndYear() {
        val title = TmdbTitle(id = 7, isMovie = true, title = "Frankenstein", posterPath = "/f.jpg", year = 2025)

        assertEquals(
            EcsPosterItem(id = 7, title = "Frankenstein", imageUrl = "https://image.tmdb.org/t/p/w500/f.jpg", subtitle = "2025"),
            title.asPosterItem(),
        )
    }

    @Test
    fun adaptationWithoutPosterOrYear_hasNeither() {
        val item = TmdbTitle(id = 8, isMovie = false, title = "Emma", posterPath = null, year = null).asPosterItem()

        assertEquals(null, item.imageUrl)
        assertEquals(null, item.subtitle)
    }
}
