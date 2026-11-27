package com.rick.ecatalogs.data.trailer

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class YouTubeTest {

    @Test
    fun youtubeIdFromUrl_readsEmbedWatchShortAndShortsUrls() {
        assertEquals("d9MyW72ELq0", youtubeIdFromUrl("https://www.youtube-nocookie.com/embed/d9MyW72ELq0?enablejsapi=1&wmode=opaque"))
        assertEquals("d9MyW72ELq0", youtubeIdFromUrl("https://www.youtube.com/watch?v=d9MyW72ELq0"))
        assertEquals("d9MyW72ELq0", youtubeIdFromUrl("https://www.youtube.com/watch?feature=share&v=d9MyW72ELq0"))
        assertEquals("d9MyW72ELq0", youtubeIdFromUrl("https://youtu.be/d9MyW72ELq0"))
        assertEquals("d9MyW72ELq0", youtubeIdFromUrl("https://www.youtube.com/shorts/d9MyW72ELq0"))
    }

    @Test
    fun youtubeIdFromUrl_returnsNullForOtherUrls() {
        assertNull(youtubeIdFromUrl(null))
        assertNull(youtubeIdFromUrl(""))
        assertNull(youtubeIdFromUrl("https://vimeo.com/123456789"))
        assertNull(youtubeIdFromUrl("https://www.youtube.com/embed/tooShort"))
        assertNull(youtubeIdFromUrl("https://www.youtube.com/embed/wayTooLongForAnId"))
    }

    @Test
    fun thumbnailAndWatchUrls_useTheVideoId() {
        assertEquals("https://img.youtube.com/vi/abcdefghijk/hqdefault.jpg", youtubeThumbnailUrl("abcdefghijk"))
        assertEquals("https://www.youtube.com/watch?v=abcdefghijk", youtubeWatchUrl("abcdefghijk"))
    }
}
