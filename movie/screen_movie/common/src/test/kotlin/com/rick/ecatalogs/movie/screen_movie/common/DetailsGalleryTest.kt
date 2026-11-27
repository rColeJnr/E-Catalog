package com.rick.ecatalogs.movie.screen_movie.common

import com.rick.ecatalogs.data.trailer.GalleryItem
import com.rick.ecatalogs.movie.data_movie.model.tmdb.movie.TmdbDetailsExtras
import org.junit.Test
import kotlin.test.assertEquals

class DetailsGalleryTest {

    @Test
    fun galleryItems_trailerThenPosterThenWideBackdrops() {
        val extras = TmdbDetailsExtras(trailerYoutubeKey = "abcdefghijk", backdrops = listOf("/b1.jpg", "/b2.jpg"))

        val items = detailsGalleryItems("/poster.jpg", extras)

        assertEquals(
            listOf(
                GalleryItem.Trailer("abcdefghijk"),
                GalleryItem.Image("https://image.tmdb.org/t/p/w780/poster.jpg"),
                GalleryItem.Image("https://image.tmdb.org/t/p/w1280/b1.jpg", wide = true),
                GalleryItem.Image("https://image.tmdb.org/t/p/w1280/b2.jpg", wide = true),
            ),
            items,
        )
    }

    @Test
    fun galleryItems_withoutTrailerOrPoster_keepWhatThereIs() {
        assertEquals(
            listOf(GalleryItem.Image("https://image.tmdb.org/t/p/w1280/b1.jpg", wide = true)),
            detailsGalleryItems("", TmdbDetailsExtras(backdrops = listOf("/b1.jpg", "/b1.jpg"))),
        )
        assertEquals(emptyList(), detailsGalleryItems(" ", TmdbDetailsExtras()))
    }
}
