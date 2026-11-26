package com.rick.ecatalogs.anime.screen_anime.common

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

/** Deep links must match the nav graphs: `com.rick.ecs://anime_details_fragment/{anime}`… */
@RunWith(RobolectricTestRunner::class)
class JikanDetailsUriTest {

    @Test
    fun anime_opensAnimeDetails() {
        val uri = jikanDetailsUri(52991, isAnime = true)

        assertEquals("com.rick.ecs", uri.scheme)
        assertEquals("anime_details_fragment", uri.host)
        assertEquals(listOf("52991"), uri.pathSegments)
    }

    @Test
    fun manga_opensMangaDetails() {
        val uri = jikanDetailsUri(126287, isAnime = false)

        assertEquals("com.rick.ecs://manga_details_fragment/126287", uri.toString())
        assertEquals("manga_details_fragment", uri.host)
    }
}
