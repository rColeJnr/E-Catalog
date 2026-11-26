package com.rick.ecatalogs.movie.screen_movie.common

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FilmTitleTest {

    @Test
    fun quotedTitle_isTakenFromCurlyQuotes() {
        assertEquals(
            "Wicked: For Good",
            filmTitleFromReviewHeadline("‘Wicked: For Good’ Review: Defying Gravity, Again"),
        )
    }

    @Test
    fun quotedTitle_withStraightQuotes() {
        assertEquals("Anora", filmTitleFromReviewHeadline("\"Anora\" Review: A Cinderella Story"))
    }

    @Test
    fun unquotedTitle_beforeReview() {
        assertEquals("The Brutalist", filmTitleFromReviewHeadline("The Brutalist Review: Building a Life"))
    }

    @Test
    fun headlineWithoutAFilm_givesNull() {
        assertNull(filmTitleFromReviewHeadline("The Best Movies of the Year So Far"))
    }
}
