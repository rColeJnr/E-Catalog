package com.rick.ecatalogs.book.data_book.data.repository.gutenberg

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator.InitializeAction
import androidx.paging.RemoteMediator.MediatorResult
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.gson.Gson
import com.rick.ecatalogs.book.data_book.data.model.asGutenbergEntity
import com.rick.ecatalogs.book.data_book.data.testing.testListCache
import com.rick.ecatalogs.book.data_book.data.testing.unimplemented
import com.rick.ecatalogs.book.data_book.database.BookDatabase
import com.rick.ecatalogs.book.data_book.database.BookTransactionRunner
import com.rick.ecatalogs.book.data_book.database.model.GutenbergEntity
import com.rick.ecatalogs.book.data_book.database.util.BookTypeConverters
import com.rick.ecatalogs.book.data_book.model.gutenberg.GutenbergFilter
import com.rick.ecatalogs.book.data_book.network.GutenbergNetworkDataSource
import com.rick.ecatalogs.book.data_book.network.model.GutenbergNetwork
import com.rick.ecatalogs.book.data_book.network.model.GutenbergResponse
import com.rick.ecatalogs.data.common.GsonParser
import com.rick.ecatalogs.data.datastore.ListCache
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Project Gutenberg books, paged from Gutendex into Room. One table holds the list for the chosen
 * filter (language / topic): switching filters must not mix in the previous filter's books.
 */
@OptIn(ExperimentalPagingApi::class)
@RunWith(RobolectricTestRunner::class)
class GutenbergRemoteMediatorTest {

    private lateinit var database: BookDatabase
    private lateinit var network: FakeGutendex
    private lateinit var listCache: ListCache
    private var favorites = emptySet<Int>()

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            BookDatabase::class.java,
        )
            .addTypeConverter(BookTypeConverters(GsonParser(Gson())))
            .allowMainThreadQueries()
            .build()
        network = FakeGutendex()
        listCache = testListCache()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun refresh_storesTheFiltersBooks_mostDownloadedFirst() = runTest {
        network.books[ALL] = listOf(book(1, downloads = 10), book(2, downloads = 500), book(3, downloads = 90))

        val result = mediator(GutenbergFilter()).load(LoadType.REFRESH, emptyState())

        assertIs<MediatorResult.Success>(result)
        assertEquals(listOf(2, 3, 1), catalogIds())
    }

    @Test
    fun switchingFilters_dropsThePreviousFiltersBooks() = runTest {
        network.books[ALL] = listOf(book(1, downloads = 900), book(2, downloads = 800))
        network.books[PORTUGUESE] = listOf(book(10, downloads = 50))
        mediator(GutenbergFilter()).load(LoadType.REFRESH, emptyState())

        mediator(GutenbergFilter(language = "pt")).load(LoadType.REFRESH, emptyState())

        // Before the fix the popular English books stayed on top of the Portuguese list
        assertEquals(listOf(10), catalogIds())
    }

    @Test
    fun switchingFilters_keepsFavoriteBooks_outOfTheList() = runTest {
        network.books[ALL] = listOf(book(1, downloads = 900), book(2, downloads = 800))
        network.books[PORTUGUESE] = listOf(book(10, downloads = 50))
        mediator(GutenbergFilter()).load(LoadType.REFRESH, emptyState())
        favorites = setOf(2)

        mediator(GutenbergFilter(language = "pt")).load(LoadType.REFRESH, emptyState())

        assertEquals(listOf(10), catalogIds())
        assertEquals(setOf(2, 10), allIds())
    }

    @Test
    fun refresh_marksOnlyTheChosenFilterFresh() = runTest {
        network.books[ALL] = listOf(book(1))
        network.books[PORTUGUESE] = listOf(book(10))
        val all = mediator(GutenbergFilter())
        val portuguese = mediator(GutenbergFilter(language = "pt"))
        all.load(LoadType.REFRESH, emptyState())

        portuguese.load(LoadType.REFRESH, emptyState())

        // Only one filter's books are on the phone: going back to "all" downloads them again
        assertEquals(InitializeAction.SKIP_INITIAL_REFRESH, portuguese.initialize())
        assertEquals(InitializeAction.LAUNCH_INITIAL_REFRESH, all.initialize())
    }

    @Test
    fun append_continuesWithTheNextPage_untilAnEmptyOne() = runTest {
        network.books[ALL] = listOf(book(1, downloads = 2), book(2, downloads = 1))
        network.secondPage = listOf(book(3))
        val subject = mediator(GutenbergFilter())
        subject.load(LoadType.REFRESH, emptyState())

        subject.load(LoadType.APPEND, stateWith(catalog()))
        val end = subject.load(LoadType.APPEND, stateWith(catalog()))

        assertEquals(listOf(1, 2, 3), catalogIds())
        assertIs<MediatorResult.Success>(end)
        assertTrue(end.endOfPaginationReached)
    }

    @Test
    fun searchResults_stayOutOfTheCatalog() = runTest {
        network.books[ALL] = listOf(book(1))
        mediator(GutenbergFilter()).load(LoadType.REFRESH, emptyState())

        database.gutenbergDao.insertOutsideCatalog(listOf(book(99).asGutenbergEntity()))

        assertEquals(listOf(1), catalogIds())
    }

    @Test
    fun booksWithNullAuthorsOrFormats_areStored() = runTest {
        // Gutendex can send null where the model has defaults (Gson ignores Kotlin defaults)
        network.raw = """{"results": [{"id": 5, "title": "Untitled", "authors": null,
            "bookshelves": null, "formats": null, "media_type": "Text", "download_count": 3}]}"""

        val result = mediator(GutenbergFilter()).load(LoadType.REFRESH, emptyState())

        assertIs<MediatorResult.Success>(result)
        val stored = catalog().single()
        assertEquals(emptyList(), stored.authors)
        assertEquals(null, stored.formats.imageJpeg)
    }

    @Test
    fun offline_isAnError_andKeepsTheStoredList() = runTest {
        network.books[ALL] = listOf(book(1))
        val subject = mediator(GutenbergFilter())
        subject.load(LoadType.REFRESH, emptyState())
        network.offline = true

        assertIs<MediatorResult.Error>(subject.load(LoadType.REFRESH, emptyState()))
        assertEquals(listOf(1), catalogIds())
    }

    private fun mediator(filter: GutenbergFilter) = GutenbergRemoteMediator(
        gutenbergDao = database.gutenbergDao,
        keysDao = database.gutenbergRemoteKeysDao,
        network = network,
        transactionRunner = BookTransactionRunner(database),
        listCache = listCache,
        filter = filter,
        favoriteIds = { favorites },
    )

    private suspend fun catalog(): List<GutenbergEntity> {
        val page = database.gutenbergDao.getGutenbergs()
            .load(PagingSource.LoadParams.Refresh(null, 100, false))
        return (page as PagingSource.LoadResult.Page).data
    }

    private suspend fun catalogIds() = catalog().map { it.id }

    private suspend fun allIds() =
        database.gutenbergDao.getGutenbergWithFilters().first().map { it.id }.toSet()

    private fun emptyState() =
        PagingState<Int, GutenbergEntity>(emptyList(), null, PagingConfig(pageSize = 20), 0)

    private fun stateWith(rows: List<GutenbergEntity>) = PagingState(
        listOf(PagingSource.LoadResult.Page(rows, null, null)), null, PagingConfig(pageSize = 20), 0,
    )

    private fun book(id: Int, downloads: Int = 0) =
        GutenbergNetwork(id = id, title = "Book $id", mediaType = "Text", downloads = downloads)

    /** Page 1 per language filter (null = all languages), [secondPage] for page 2. */
    private class FakeGutendex : GutenbergNetworkDataSource by unimplemented() {
        val books = mutableMapOf<String?, List<GutenbergNetwork>>()
        var secondPage = emptyList<GutenbergNetwork>()
        var raw: String? = null
        var offline = false

        override suspend fun fetchBooks(page: Int, languages: String?, topic: String?): GutenbergResponse {
            if (offline) throw IOException("offline")
            raw?.let { return Gson().fromJson(it, GutenbergResponse::class.java) }
            return GutenbergResponse(
                when (page) {
                    1 -> books[languages].orEmpty()
                    2 -> secondPage
                    else -> emptyList()
                },
            )
        }
    }
}

private val ALL: String? = null
private const val PORTUGUESE = "pt"
