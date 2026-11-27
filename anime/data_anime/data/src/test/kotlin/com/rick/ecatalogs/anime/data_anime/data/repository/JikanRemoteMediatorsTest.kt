package com.rick.ecatalogs.anime.data_anime.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator.MediatorResult
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.gson.Gson
import com.rick.ecatalogs.anime.data_anime.data.model.asEntity
import com.rick.ecatalogs.anime.data_anime.data.repository.anime.AnimeRemoteMediator
import com.rick.ecatalogs.anime.data_anime.data.repository.anime.AnimeRepositoryImpl
import com.rick.ecatalogs.anime.data_anime.data.repository.manga.MangaRemoteMediator
import com.rick.ecatalogs.anime.data_anime.data.repository.manga.MangaRepositoryImpl
import com.rick.ecatalogs.anime.data_anime.data.testing.unimplemented
import com.rick.ecatalogs.anime.data_anime.database.JikanDatabase
import com.rick.ecatalogs.anime.data_anime.database.JikanTransactionRunner
import com.rick.ecatalogs.anime.data_anime.database.model.AnimeEntity
import com.rick.ecatalogs.anime.data_anime.database.model.MangaEntity
import com.rick.ecatalogs.anime.data_anime.database.util.JikanConverters
import com.rick.ecatalogs.anime.data_anime.network.JikanNetworkDataSource
import com.rick.ecatalogs.anime.data_anime.network.model.AnimeNetwork
import com.rick.ecatalogs.anime.data_anime.network.model.AnimeResponse
import com.rick.ecatalogs.anime.data_anime.network.model.MangaResponse
import com.rick.ecatalogs.data.common.GsonParser
import com.rick.ecatalogs.data.datastore.CachedList
import com.rick.ecatalogs.data.datastore.ListCache
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.data.datastore.UserPreferences
import com.rick.ecatalogs.data.datastore.test.InMemoryDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Top anime and top manga, paged from Jikan into Room: the catalog keeps Jikan's order, a
 * refresh replaces the list (keeping favorites), and search results stay out of it.
 */
@OptIn(ExperimentalPagingApi::class)
@RunWith(RobolectricTestRunner::class)
class JikanRemoteMediatorsTest {

    private lateinit var database: JikanDatabase
    private lateinit var network: FakeJikanNetwork
    private lateinit var listCache: ListCache
    private lateinit var preferences: PreferencesDataSource
    private lateinit var animeRepository: AnimeRepositoryImpl
    private lateinit var mangaRepository: MangaRepositoryImpl
    private var favorites = emptySet<Int>()
    private lateinit var anime: AnimeRemoteMediator
    private lateinit var manga: MangaRemoteMediator

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            JikanDatabase::class.java,
        )
            .addTypeConverter(JikanConverters(GsonParser(Gson())))
            .allowMainThreadQueries()
            .build()
        network = FakeJikanNetwork()
        preferences = PreferencesDataSource(InMemoryDataStore(UserPreferences.getDefaultInstance()))
        listCache = ListCache(preferences)
        val runner = JikanTransactionRunner(database)
        animeRepository = AnimeRepositoryImpl(
            keysDao = database.animeRemoteKeysDao,
            network = network,
            animeDao = database.animeDao,
            transactionRunner = runner,
            listCache = listCache,
            preferences = preferences,
        )
        mangaRepository = MangaRepositoryImpl(
            keysDao = database.mangaRemoteKeysDao,
            mangaDao = database.mangaDao,
            network = network,
            transactionRunner = runner,
            listCache = listCache,
            preferences = preferences,
        )
        anime = AnimeRemoteMediator(
            animeDao = database.animeDao,
            keysDao = database.animeRemoteKeysDao,
            network = network,
            transactionRunner = runner,
            listCache = listCache,
            favoriteIds = { favorites },
        )
        manga = MangaRemoteMediator(
            mangaDao = database.mangaDao,
            keysDao = database.mangaRemoteKeysDao,
            network = network,
            transactionRunner = runner,
            listCache = listCache,
            favoriteIds = { favorites },
        )
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun animeRefresh_storesThePage_inJikanOrder_andMarksItFresh() = runTest {
        network.anime[1] = listOf(30, 10, 20)

        val result = anime.load(LoadType.REFRESH, emptyState())

        assertIs<MediatorResult.Success>(result)
        assertEquals(listOf(30, 10, 20), animeCatalog().map { it.id })
        assertTrue(listCache.isFresh(CachedList.ANIME))
    }

    @Test
    fun animeAppend_continuesWithTheNextPage() = runTest {
        network.anime[1] = listOf(1, 2)
        network.anime[2] = listOf(3)
        anime.load(LoadType.REFRESH, emptyState())

        anime.load(LoadType.APPEND, stateWith(animeCatalog()))

        assertEquals(listOf(1, 2, 3), animeCatalog().map { it.id })
    }

    @Test
    fun animeAppend_afterAnEmptyPage_isTheEnd() = runTest {
        network.anime[1] = listOf(1)
        anime.load(LoadType.REFRESH, emptyState())

        val result = anime.load(LoadType.APPEND, stateWith(animeCatalog()))

        assertIs<MediatorResult.Success>(result)
        assertTrue(result.endOfPaginationReached)
    }

    @Test
    fun animeRefresh_replacesTheList_butKeepsFavorites() = runTest {
        network.anime[1] = listOf(1, 2, 3)
        anime.load(LoadType.REFRESH, emptyState())

        favorites = setOf(3)
        network.anime[1] = listOf(4)
        anime.load(LoadType.REFRESH, emptyState())

        assertEquals(listOf(4), animeCatalog().map { it.id })
        assertEquals(setOf(3, 4), database.animeDao.getAnimeWithFilters().first().map { it.id }.toSet())
    }

    @Test
    fun animeSearchResults_stayOutOfTheCatalog() = runTest {
        network.anime[1] = listOf(1)
        anime.load(LoadType.REFRESH, emptyState())

        database.animeDao.insertOutsideCatalog(listOf(animeNetwork(99).asEntity()))

        assertEquals(listOf(1), animeCatalog().map { it.id })
    }

    @Test
    fun animeOffline_isAnError_andKeepsTheStoredList() = runTest {
        network.anime[1] = listOf(1)
        anime.load(LoadType.REFRESH, emptyState())
        network.offline = true

        assertIs<MediatorResult.Error>(anime.load(LoadType.REFRESH, emptyState()))
        assertEquals(listOf(1), animeCatalog().map { it.id })
    }

    @Test
    fun mangaRefresh_storesThePage_includingUnscoredManga() = runTest {
        network.manga[1] = listOf(5, 6)

        manga.load(LoadType.REFRESH, emptyState())

        val stored = mangaCatalog()
        assertEquals(listOf(5, 6), stored.map { it.id })
        assertEquals(0.0, stored.last().score) // Jikan sends null for unscored manga
        assertTrue(listCache.isFresh(CachedList.MANGA))
    }

    @Test
    fun mangaRefresh_replacesTheList_butKeepsFavorites() = runTest {
        network.manga[1] = listOf(5, 6)
        manga.load(LoadType.REFRESH, emptyState())

        favorites = setOf(5)
        network.manga[1] = listOf(7)
        manga.load(LoadType.REFRESH, emptyState())

        assertEquals(listOf(7), mangaCatalog().map { it.id })
        assertEquals(setOf(5, 7), database.mangaDao.getMangaWithFilters().first().map { it.id }.toSet())
    }

    @Test
    fun animeSync_firstRun_storesThePageAndNotifiesNothing() = runTest {
        network.anime[1] = listOf(1, 2)

        val new = animeRepository.syncFirstPage()

        assertTrue(new.isEmpty(), "a first sync shouldn't notify the whole list")
        assertEquals(listOf(1, 2), animeCatalog().map { it.id })
        assertTrue(listCache.isFresh(CachedList.ANIME))
    }

    @Test
    fun animeSync_whenNewAnimeEnterTheList_thenOnlyTheyAreReturned() = runTest {
        network.anime[1] = listOf(1, 2)
        animeRepository.syncFirstPage()
        network.anime[1] = listOf(3, 1, 4)

        val new = animeRepository.syncFirstPage()

        assertEquals(listOf(3, 4), new.map { it.id })
        assertEquals(listOf(3, 1, 4), animeCatalog().map { it.id })
    }

    @Test
    fun animeSync_whenNothingChanged_thenNothingIsReturned() = runTest {
        network.anime[1] = listOf(1, 2)
        animeRepository.syncFirstPage()

        assertTrue(animeRepository.syncFirstPage().isEmpty())
        assertEquals(listOf(1, 2), animeCatalog().map { it.id })
    }

    @Test
    fun animeSync_offline_throwsAndKeepsTheList() = runTest {
        network.anime[1] = listOf(1)
        animeRepository.syncFirstPage()
        network.offline = true

        assertFailsWith<IOException> { animeRepository.syncFirstPage() }
        assertEquals(listOf(1), animeCatalog().map { it.id })
    }

    @Test
    fun mangaSync_whenNewMangaEnterTheList_thenOnlyTheyAreReturned() = runTest {
        network.manga[1] = listOf(5, 6)
        assertTrue(mangaRepository.syncFirstPage().isEmpty())
        network.manga[1] = listOf(7, 5)

        val new = mangaRepository.syncFirstPage()

        assertEquals(listOf(7), new.map { it.id })
        assertEquals(listOf(7, 5), mangaCatalog().map { it.id })
        assertTrue(listCache.isFresh(CachedList.MANGA))
    }

    private suspend fun animeCatalog(): List<AnimeEntity> = loadAll(database.animeDao.getAnime())

    private suspend fun mangaCatalog(): List<MangaEntity> = loadAll(database.mangaDao.getManga())

    private suspend fun <T : Any> loadAll(source: PagingSource<Int, T>): List<T> {
        val page = source.load(PagingSource.LoadParams.Refresh(null, 100, false))
        return (page as PagingSource.LoadResult.Page).data
    }

    private fun <T : Any> emptyState() =
        PagingState<Int, T>(emptyList(), null, PagingConfig(pageSize = 25), 0)

    // The key type is explicit: inferred from the null keys it would be Nothing
    private fun <T : Any> stateWith(rows: List<T>) = PagingState<Int, T>(
        listOf(PagingSource.LoadResult.Page(rows, null, null)), null, PagingConfig(pageSize = 25), 0,
    )

    private fun animeNetwork(id: Int): AnimeNetwork =
        gson.fromJson("""{"mal_id": $id, "title": "Anime $id"}""", AnimeNetwork::class.java)

    /** Serves Jikan's top lists as Jikan sends them (JSON, parsed with Gson like the app). */
    private class FakeJikanNetwork : JikanNetworkDataSource by unimplemented() {
        val anime = mutableMapOf<Int, List<Int>>()
        val manga = mutableMapOf<Int, List<Int>>()
        var offline = false

        override suspend fun fetchTopAnime(page: Int, filter: String): AnimeResponse {
            if (offline) throw IOException("offline")
            val items = anime[page].orEmpty().joinToString { id ->
                """{"mal_id": $id, "title": "Anime $id", "images": {"jpg": {"image_url": "https://img/$id.jpg"}}, "genres": []}"""
            }
            return gson.fromJson(page(items), AnimeResponse::class.java)
        }

        override suspend fun fetchTopManga(page: Int, filter: String): MangaResponse {
            if (offline) throw IOException("offline")
            val items = manga[page].orEmpty().joinToString { id ->
                """{"mal_id": $id, "url": "https://mal/$id", "title": "Manga $id", "type": "Manga",
                    "publishing": false, "published": {"string": "2001"}, "score": ${if (id % 2 == 0) "null" else "8.5"},
                    "authors": [], "serializations": [], "genres": [], "themes": []}"""
            }
            return gson.fromJson(page(items), MangaResponse::class.java)
        }

        private fun page(items: String) =
            """{"pagination": {"current_page": 1, "has_next_page": true, "last_visible_page": 9,
                "items": {"count": 25, "total": 200, "per_page": 25}}, "data": [$items]}"""
    }
}

private val gson = Gson()
