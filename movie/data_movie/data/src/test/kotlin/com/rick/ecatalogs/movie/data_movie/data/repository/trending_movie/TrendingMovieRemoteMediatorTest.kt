package com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie

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
import com.rick.ecatalogs.data.common.GsonParser
import com.rick.ecatalogs.data.datastore.CachedList
import com.rick.ecatalogs.data.datastore.ListCache
import com.rick.ecatalogs.movie.data_movie.data.model.asTrendingMovieEntity
import com.rick.ecatalogs.movie.data_movie.data.testing.testListCache
import com.rick.ecatalogs.movie.data_movie.data.testing.unimplemented
import com.rick.ecatalogs.movie.data_movie.database.MovieDatabase
import com.rick.ecatalogs.movie.data_movie.database.MovieTransactionRunner
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingMovieEntity
import com.rick.ecatalogs.movie.data_movie.database.util.NyTimesConverters
import com.rick.ecatalogs.movie.data_movie.network.TmdbNetworkDataSource
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingMovieNetwork
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingMovieResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Trending movies paged from TMDB into Room: the catalog keeps TMDB's order, a refresh replaces
 * the list (keeping favorites), and search results never show up in it.
 */
@OptIn(ExperimentalPagingApi::class)
@RunWith(RobolectricTestRunner::class)
class TrendingMovieRemoteMediatorTest {

    private lateinit var database: MovieDatabase
    private lateinit var network: FakeTmdbNetwork
    private lateinit var listCache: ListCache
    private var favorites = emptySet<Int>()
    private lateinit var subject: TrendingMovieRemoteMediator

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MovieDatabase::class.java,
        )
            .addTypeConverter(NyTimesConverters(GsonParser(Gson())))
            .allowMainThreadQueries()
            .build()
        network = FakeTmdbNetwork()
        listCache = testListCache()
        subject = TrendingMovieRemoteMediator(
            network = network,
            keysDao = database.trendingMovieRemoteKeysDao,
            trendingMovieDao = database.trendingMovieDao,
            transactionRunner = MovieTransactionRunner(database),
            listCache = listCache,
            favoriteIds = { favorites },
        )
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun refresh_storesThePage_inTmdbOrder() = runTest {
        network.pages[1] = listOf(movie(30), movie(10), movie(20))

        val result = subject.load(LoadType.REFRESH, emptyState())

        assertIs<MediatorResult.Success>(result)
        assertFalse(result.endOfPaginationReached)
        assertEquals(listOf(30, 10, 20), catalogIds())
    }

    @Test
    fun append_loadsTheNextPage_afterTheFirst() = runTest {
        network.pages[1] = listOf(movie(1), movie(2))
        network.pages[2] = listOf(movie(3), movie(4))
        subject.load(LoadType.REFRESH, emptyState())

        subject.load(LoadType.APPEND, stateWith(catalogRows()))

        assertEquals(listOf(1, 2), network.requestedPages)
        assertEquals(listOf(1, 2, 3, 4), catalogIds())
    }

    @Test
    fun refresh_replacesTheList_butKeepsFavorites() = runTest {
        network.pages[1] = listOf(movie(1), movie(2), movie(3))
        subject.load(LoadType.REFRESH, emptyState())

        favorites = setOf(2)
        network.pages[1] = listOf(movie(4), movie(5))
        subject.load(LoadType.REFRESH, emptyState())

        assertEquals(listOf(4, 5), catalogIds())
        // 1 and 3 dropped out of the trending list; favorite 2 stays for the favorites screen
        assertEquals(setOf(2, 4, 5), allIds())
    }

    @Test
    fun searchResults_stayOutOfTheCatalog() = runTest {
        network.pages[1] = listOf(movie(1))
        subject.load(LoadType.REFRESH, emptyState())

        database.trendingMovieDao.insertOutsideCatalog(listOf(movie(99).asTrendingMovieEntity()))

        assertEquals(listOf(1), catalogIds())
        assertEquals(setOf(1, 99), allIds())
    }

    @Test
    fun refresh_marksTheListFresh() = runTest {
        network.pages[1] = listOf(movie(1))

        assertEquals(InitializeAction.LAUNCH_INITIAL_REFRESH, subject.initialize())
        subject.load(LoadType.REFRESH, emptyState())

        assertTrue(listCache.isFresh(CachedList.TRENDING_MOVIES))
        assertEquals(InitializeAction.SKIP_INITIAL_REFRESH, subject.initialize())
    }

    @Test
    fun offline_isAnError_andKeepsTheStoredList() = runTest {
        network.pages[1] = listOf(movie(1))
        subject.load(LoadType.REFRESH, emptyState())

        network.offline = true
        val result = subject.load(LoadType.REFRESH, emptyState())

        assertIs<MediatorResult.Error>(result)
        assertEquals(listOf(1), catalogIds())
    }

    @Test
    fun prepend_fromTheFirstPage_isTheEnd() = runTest {
        network.pages[1] = listOf(movie(1))
        subject.load(LoadType.REFRESH, emptyState())

        val result = subject.load(LoadType.PREPEND, stateWith(catalogRows()))

        assertIs<MediatorResult.Success>(result)
        assertTrue(result.endOfPaginationReached)
    }

    private suspend fun catalogRows(): List<TrendingMovieEntity> {
        val page = database.trendingMovieDao.getTrendingMovie().load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 100, placeholdersEnabled = false),
        )
        return (page as PagingSource.LoadResult.Page).data
    }

    private suspend fun catalogIds() = catalogRows().map { it.id }

    private suspend fun allIds() =
        database.trendingMovieDao.getTrendingMovieWithFilters().first().map { it.id }.toSet()

    private fun emptyState() = PagingState<Int, TrendingMovieEntity>(
        pages = emptyList(), anchorPosition = null, config = PagingConfig(pageSize = 20), leadingPlaceholderCount = 0,
    )

    // The key type is explicit: inferred from the null keys it would be Nothing
    private fun stateWith(rows: List<TrendingMovieEntity>) = PagingState<Int, TrendingMovieEntity>(
        pages = listOf(PagingSource.LoadResult.Page(data = rows, prevKey = null, nextKey = null)),
        anchorPosition = null,
        config = PagingConfig(pageSize = 20),
        leadingPlaceholderCount = 0,
    )

    private fun movie(id: Int) = TrendingMovieNetwork(
        id = id, adult = false, image = null, title = "Movie $id", overview = null, popularity = null,
        releaseDate = null, voteAverage = null, voteCount = null,
    )

    private class FakeTmdbNetwork : TmdbNetworkDataSource by unimplemented() {
        val pages = mutableMapOf<Int, List<TrendingMovieNetwork>>()
        val requestedPages = mutableListOf<Int>()
        var offline = false

        override suspend fun fetchTrendingMovies(page: Int, language: String): TrendingMovieResponse {
            if (offline) throw IOException("offline")
            requestedPages += page
            return TrendingMovieResponse(page, pages[page].orEmpty())
        }
    }
}
