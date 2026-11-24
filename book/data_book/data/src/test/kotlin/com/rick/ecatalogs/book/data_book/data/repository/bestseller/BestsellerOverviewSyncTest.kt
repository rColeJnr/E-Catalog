package com.rick.ecatalogs.book.data_book.data.repository.bestseller

import com.rick.ecatalogs.book.data_book.network.model.BestsellerOverviewResponse
import com.rick.ecatalogs.book.data_book.network.model.OverviewBook
import com.rick.ecatalogs.book.data_book.network.model.OverviewList
import com.rick.ecatalogs.book.data_book.network.model.OverviewResults
import com.rick.ecatalogs.book.data_book.network.model.rankedIdsByList
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BestsellerOverviewSyncTest {

    @Test
    fun unchangedTop_doesNotNeedFullFetch() {
        assertFalse(
            listChanged(
                overviewIds = listOf("a", "b", "c"),
                storedIds = listOf("a", "b", "c", "d", "e"),
            )
        )
    }

    @Test
    fun newBookOrNewOrder_needsFullFetch() {
        assertTrue(listChanged(listOf("a", "x", "c"), listOf("a", "b", "c", "d")))
        assertTrue(listChanged(listOf("b", "a", "c"), listOf("a", "b", "c", "d")))
    }

    @Test
    fun nothingStoredOrListMissingFromOverview_needsFullFetch() {
        assertTrue(listChanged(listOf("a"), storedIds = emptyList()))
        assertTrue(listChanged(overviewIds = null, storedIds = listOf("a")))
        assertTrue(listChanged(overviewIds = emptyList(), storedIds = listOf("a")))
    }

    @Test
    fun overview_isRankedPerList_skippingIncompleteEntries() {
        val overview = BestsellerOverviewResponse(
            OverviewResults(
                lists = listOf(
                    OverviewList(
                        listNameEncoded = "business-books",
                        books = listOf(
                            OverviewBook(id = "second", rank = 2),
                            OverviewBook(id = null, rank = 3),
                            OverviewBook(id = "first", rank = 1),
                        ),
                    ),
                    OverviewList(listNameEncoded = null, books = listOf(OverviewBook("x", 1))),
                ),
            ),
        )

        assertEquals(mapOf("business-books" to listOf("first", "second")), overview.rankedIdsByList())
    }
}
