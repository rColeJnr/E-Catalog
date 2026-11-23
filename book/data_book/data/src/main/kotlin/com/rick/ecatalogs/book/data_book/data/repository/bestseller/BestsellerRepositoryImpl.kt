package com.rick.ecatalogs.book.data_book.data.repository.bestseller

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
    private val preferences: PreferencesDataSource,
) : BestsellerRepository {

    override suspend fun syncAllGenres(): Map<BestsellerGenre, List<Bestseller>> = buildMap {
        for (genre in BestsellerGenre.entries) {
            val books = network.fetchBestsellers(
                date = CURRENT_LIST,
                bookGenre = genre.listName,
            ).results.books
            val fetchedIds = books.map(BestsellerNetwork::id).toSet()
            if (fetchedIds.isEmpty()) continue

            if (fetchedIds != bestsellerDao.bestsellerIds(genre.listName).toSet()) {
                bestsellerDao.replaceBestsellers(
                    genre = genre.listName,
                    books = books.map { it.asBestsellerEntity(genre.listName) },
                )
            }

            val previouslySeen = preferences.notifiedBestsellerIds(genre.listName)
            preferences.setNotifiedBestsellerIds(genre.listName, fetchedIds)
            // First sync of this genre: record it without notifying about the whole list
            if (previouslySeen.isEmpty()) continue
            val newBooks = books.filter { it.id !in previouslySeen }
            if (newBooks.isNotEmpty()) {
                put(genre, newBooks.map { it.asBestsellerEntity(genre.listName).asBestseller() })
            }
        }
    }

    override fun getBestseller(
        bookGenre: String,
        date: String
    ): Flow<List<Bestseller>> = channelFlow {

        try {
            val bestsellers = network.fetchBestsellers(
                date = date,
                bookGenre = bookGenre,
            ).results.books

            bestsellerDao.replaceBestsellers(
                genre = bookGenre,
                books = bestsellers.map { it.asBestsellerEntity(bookGenre) },
            )

            bestsellerDao.getBestsellers(bookGenre)
                .collectLatest { bestsellerEntities ->
                    send(bestsellerEntities.map(BestsellerEntity::asBestseller)) }
        } catch (e: IOException) {
            Log.e(TAG, e.localizedMessage ?: "IOException")
        } catch (e: HttpException) {
            Log.e(TAG, e.localizedMessage ?: "HTTPException")
        }
    }

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
private const val CURRENT_LIST = "current"