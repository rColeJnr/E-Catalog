package com.rick.ecatalogs.anime.data_anime.data.repository.browse

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class YoutubeIdTest {

    @Test
    fun readsTheIdFromJikanEmbedUrls() {
        assertEquals(
            "qig4KOK2R2g",
            youtubeIdFromEmbedUrl(
                "https://www.youtube-nocookie.com/embed/qig4KOK2R2g?enablejsapi=1&wmode=opaque&autoplay=1"
            ),
        )
    }

    @Test
    fun otherUrls_giveNull() {
        assertNull(youtubeIdFromEmbedUrl("https://myanimelist.net/anime/1"))
    }
}
