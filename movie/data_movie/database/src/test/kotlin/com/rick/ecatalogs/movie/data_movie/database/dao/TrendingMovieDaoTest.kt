package com.rick.ecatalogs.movie.data_movie.database.dao

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.gson.Gson
import com.rick.ecatalogs.data.common.GsonParser
import com.rick.ecatalogs.movie.data_movie.database.MovieDatabase
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingMovieEntity
import com.rick.ecatalogs.movie.data_movie.database.util.NyTimesConverters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

/** The catalog list is the rows with a position, in that order; other rows are kept apart. */
@RunWith(RobolectricTestRunner::class)
class TrendingMovieDaoTest {

    private lateinit var database: MovieDatabase
    private lateinit var dao: TrendingMovieDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MovieDatabase::class.java,
        )
            .addTypeConverter(NyTimesConverters(GsonParser(Gson())))
            .allowMainThreadQueries()
            .build()
        dao = database.trendingMovieDao
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun catalog_isThePositionedRows_inPositionOrder() = runTest {
        dao.upsertTrendingMovie(listOf(movie(1, position = 2), movie(2, position = 0), movie(3, position = 1)))
        dao.insertOutsideCatalog(listOf(movie(4)))

        assertEquals(listOf(2, 3, 1), catalogIds())
    }

    @Test
    fun searchResults_neverReplaceARowOfTheCatalog() = runTest {
        dao.upsertTrendingMovie(listOf(movie(1, position = 0, title = "Dune")))

        dao.insertOutsideCatalog(listOf(movie(1, title = "Dune (search)"), movie(2)))

        assertEquals(listOf(1), catalogIds())
        assertEquals("Dune", allRows().single { it.id == 1 }.title)
    }

    @Test
    fun refresh_dropsTheOldList_butKeepsFavorites() = runTest {
        dao.upsertTrendingMovie(listOf(movie(1, position = 0), movie(2, position = 1)))
        dao.insertOutsideCatalog(listOf(movie(3), movie(4)))

        dao.clearCatalog()
        dao.deleteOutsideCatalog(keepIds = setOf(2, 4))
        dao.upsertTrendingMovie(listOf(movie(5, position = 0)))

        assertEquals(listOf(5), catalogIds())
        assertEquals(setOf(2, 4, 5), allRows().map { it.id }.toSet())
    }

    private suspend fun catalogIds(): List<Int> {
        val result = dao.getTrendingMovie().load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 50, placeholdersEnabled = false),
        )
        return (result as PagingSource.LoadResult.Page).data.map { it.id }
    }

    private suspend fun allRows() = dao.getTrendingMovieWithFilters().first()

    private fun movie(id: Int, position: Int? = null, title: String = "Movie $id") = TrendingMovieEntity(
        id = id,
        adult = false,
        title = title,
        overview = "",
        image = "",
        popularity = 0.0,
        releaseDate = "",
        voteAverage = 0.0,
        voteCount = 0,
        catalogPosition = position,
    )
}
