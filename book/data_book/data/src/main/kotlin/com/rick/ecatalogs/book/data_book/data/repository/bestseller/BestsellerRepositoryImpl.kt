package com.rick.ecatalogs.book.data_book.data.repository.bestseller

import com.rick.ecatalogs.book.data_book.database.dao.BestsellerRemoteKeysDao
import com.rick.ecatalogs.book.data_book.database.BookTransactionRunner
import androidx.paging.map
import androidx.paging.PagingData
import androidx.paging.PagingConfig
import androidx.paging.Pager
import androidx.paging.ExperimentalPagingApi
import com.rick.ecatalogs.data.datastore.CachedList
import com.rick.ecatalogs.data.datastore.ListCache
import com.rick.ecatalogs.book.data_book.network.model.rankedIdsByList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.delay
import android.util.Log
import com.rick.ecatalogs.book.data_book.network.BestsellerNetworkDataSource
import com.rick.ecatalogs.book.data_book.network.model.BestsellerNetwork
import com.rick.ecatalogs.book.data_book.data.model.asBestsellerEntity
import com.rick.ecatalogs.book.data_book.database.dao.BestsellerDao
import com.rick.ecatalogs.book.data_book.database.model.BestsellerEntity
import com.rick.ecatalogs.book.data_book.database.model.asBestseller
import com.rick.ecatalogs.book.data_book.model.bestseller.Bestseller
import com.rick.ecatalogs.book.data_book.model.bestseller.BestsellerGenre
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

internal class BestsellerRepositoryImpl @Inject constructor(
    private val bestsellerDao: BestsellerDao,
    private val network: BestsellerNetworkDataSource,
    private val keysDao: BestsellerRemoteKeysDao,
    private val transactionRunner: BookTransactionRunner,
    private val preferences: PreferencesDataSource,
    private val listCache: ListCache,
) : BestsellerRepository {

    /**
     * One overview request finds which lists changed since they were stored; only those lists
     * are then fetched in full. The lists update weekly, so most syncs make a single request
     * instead of one per genre. Notifications work as before (new books in a changed list).
     */
    override suspend fun syncAllGenres(): Map<BestsellerGenre, List<Bestseller>> {
        val overview = network.fetchOverview().rankedIdsByList()
        val changed = BestsellerGenre.entries.filter { genre ->
            val storedIds = bestsellerDao.getBestsellers(genre.listName).first().map { it.id }
            listChanged(overviewIds = overview[genre.listName], storedIds = storedIds)
        }
        // Unchanged lists were just verified against NYT: screens can keep showing them
        (BestsellerGenre.entries - changed.toSet()).forEach {
            listCache.markRefreshed(CachedList.bestseller(it.listName))
        }
        // NYT allows 5 requests a minute: space the full fetches out when there are more
        val throttle = 1 + changed.size > NYT_REQUESTS_PER_MINUTE

        return buildMap {
            changed.forEachIndexed { index, genre ->
                if (throttle && index > 0) delay(NYT_THROTTLE_DELAY_MILLIS)
                val newBooks = syncGenre(genre)
                if (newBooks.isNotEmpty()) put(genre, newBooks)
            }
        }
    }

    /** Replaces the stored list and returns the books that weren't in it at the last sync. */
    private suspend fun syncGenre(genre: BestsellerGenre): List<Bestseller> {
        val response = network.fetchBestsellers(
            date = CURRENT_LIST,
            bookGenre = genre.listName,
        )
        val books = response.results.books
        val fetchedIds = books.map(BestsellerNetwork::id).toSet()
        if (fetchedIds.isEmpty()) return emptyList()

        // The first page, as the screen's Paging would store it (further pages load on scroll)
        writeBestsellerPage(
            genre = genre.listName,
            offset = 0,
            response = response,
            isRefresh = true,
            bestsellerDao = bestsellerDao,
            keysDao = keysDao,
            transactionRunner = transactionRunner,
        )
        listCache.markRefreshed(CachedList.bestseller(genre.listName))

        val previouslySeen = preferences.notifiedBestsellerIds(genre.listName)
        preferences.setNotifiedBestsellerIds(genre.listName, fetchedIds)
        // First sync of this genre: record it without notifying about the whole list
        if (previouslySeen.isEmpty()) return emptyList()
        return books.filter { it.id !in previouslySeen }
            .map { it.asBestsellerEntity(genre.listName).asBestseller() }
    }

    /**
     * The genre's list as Paging data: the stored list is shown, and it's downloaded (page by
     * page, 20 books each) when it's missing or older than the sync interval.
     */
    @OptIn(ExperimentalPagingApi::class)
    override fun getBestsellerPaging(bookGenre: String): Flow<PagingData<Bestseller>> = Pager(
        config = PagingConfig(
            pageSize = BESTSELLER_PAGE_SIZE,
            initialLoadSize = BESTSELLER_PAGE_SIZE,
            enablePlaceholders = false,
        ),
        remoteMediator = BestsellerRemoteMediator(
            bestsellerDao = bestsellerDao,
            keysDao = keysDao,
            network = network,
            transactionRunner = transactionRunner,
            listCache = listCache,
            genre = bookGenre,
        ),
        pagingSourceFactory = { bestsellerDao.bestsellerPagingSource(bookGenre) },
    ).flow.map { page -> page.map(BestsellerEntity::asBestseller) }

    /** The genre's stored list (no network: [getBestsellerPaging] downloads it). */
    override fun getBestseller(
        bookGenre: String,
        date: String
    ): Flow<List<Bestseller>> = bestsellerDao.getBestsellers(bookGenre)
        .map { entities -> entities.map(BestsellerEntity::asBestseller) }

    override fun getBestsellerById(id: String): Flow<Bestseller?> =
        bestsellerDao.getBestsellerById(id).map { it?.asBestseller() }

    override fun getBestsellerFavorites(favorites: Set<String>): Flow<List<Bestseller>> =
        bestsellerDao.getBestsellerWithFilters(
            filterById = true,
            filterIds = favorites
        )
            .map { bestsellerEntities -> bestsellerEntities.map { it.asBestseller() } }

//    override fun searchBestseller(query: String): Flow<List<Bestseller>> = channelFlow {
//        try {
//            val searchResponse = network.se(
//                query = query,
//                max_score = 10
//            ).data
//
//            mangaDao.clearManga(searchResponse.map(MangaNetwork::id))
//
//            mangaDao.upsertManga(searchResponse.map(MangaNetwork::asEntity))
//
//            mangaDao.getMangaWithFilters(searchResponse.map(MangaNetwork::id).toSet())
//                .collectLatest { animeEntities -> send(animeEntities.map(MangaEntity::asManga)) }
//
//        } catch (e: IOException) {
//            Log.e(TAG, e.localizedMessage ?: "IOException")
//        } catch (e: HttpException) {
//            Log.e(TAG, e.localizedMessage ?: "HTTPException")
//        }
//    }
}

private const val TAG = "BestsellerRepositoryImpl"
private const val NYT_REQUESTS_PER_MINUTE = 5
private const val NYT_THROTTLE_DELAY_MILLIS = 13_000L

/**
 * Whether a list needs fetching in full: the overview's top books (in rank order) differ from
 * the top of the stored list, nothing is stored yet, or the overview didn't include the list.
 */
internal fun listChanged(overviewIds: List<String>?, storedIds: List<String>): Boolean {
    if (overviewIds.isNullOrEmpty() || storedIds.isEmpty()) return true
    return overviewIds != storedIds.take(overviewIds.size)
}