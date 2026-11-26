package com.rick.ecatalogs.game.data_game.network

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IgdbQueriesTest {

    @Test
    fun popularIds_pagesByOffset() {
        assertTrue("offset 0;" in IgdbQueries.popularIds(page = 1))
        assertTrue("offset ${IgdbQueries.PAGE_SIZE};" in IgdbQueries.popularIds(page = 2))
    }

    @Test
    fun gamesByIds_asksForEveryIdAndLeavesOutAdultGames() {
        val query = IgdbQueries.gamesByIds(listOf(3, 1, 2))
        assertTrue("where id = (3,1,2)" in query)
        assertTrue("themes != (42)" in query)
        assertTrue(query.endsWith("limit 3;"))
    }

    @Test
    fun search_dropsQuotesAndBackslashes() {
        val query = IgdbQueries.search("say \"hi\" \\o/")
        assertTrue(query.startsWith("search \"say hi o/\";"))
        assertFalse("sort" in query) // IGDB rejects search + sort
    }

    @Test
    fun comingSoon_isAfterNow_andRecentlyReleased_isBefore() {
        val now = 1_800_000_000L
        assertTrue("first_release_date > $now" in IgdbQueries.comingSoon(now))
        assertTrue("first_release_date <= $now" in IgdbQueries.recentlyReleased(now))
    }

    @Test
    fun everyQuery_endsWithASemicolon() {
        listOf(
            IgdbQueries.popularIds(1), IgdbQueries.gamesByIds(listOf(1)), IgdbQueries.comingSoon(0),
            IgdbQueries.recentlyReleased(0), IgdbQueries.GENRES, IgdbQueries.byGenre(5, 2),
            IgdbQueries.details(7), IgdbQueries.search("zelda"),
        ).forEach { assertEquals(';', it.trimEnd().last(), it) }
    }
}
