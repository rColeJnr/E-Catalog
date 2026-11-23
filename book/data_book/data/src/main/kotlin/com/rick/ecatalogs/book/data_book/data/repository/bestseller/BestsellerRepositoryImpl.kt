package com.rick.ecatalogs.book.data_book.data.repository.bestseller

import android.util.Log
import com.rick.ecatalogs.book.data_book.network.BestsellerNetworkDataSource
import com.rick.ecatalogs.book.data_book.network.model.BestsellerNetwork
import com.rick.ecatalogs.book.data_book.data.model.asBestsellerEntity
import com.rick.ecatalogs.book.data_book.database.dao.BestsellerDao
import com.rick.ecatalogs.book.data_book.database.model.BestsellerEntity
import com.rick.ecatalogs.book.data_book.database.model.asBestseller
import com.rick.ecatalogs.book.data_book.model.bestseller.Bestseller
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

internal class BestsellerRepositoryImpl @Inject constructor(
    private val bestsellerDao: BestsellerDao,
    private val network: BestsellerNetworkDataSource
) : BestsellerRepository {

    override fun getBestseller(
        bookGenre: String,
        apiKey: String,
        date: String
    ): Flow<List<Bestseller>> = channelFlow {

        try {
            val bestsellers = network.fetchBestsellers(
                date = date,
                bookGenre = bookGenre,
                apiKey = apiKey
            ).results.books

            bestsellerDao.clearBestsellers()

            bestsellerDao.upsertBestsellers(bestsellers.map(BestsellerNetwork::asBestsellerEntity))

            bestsellerDao.getBestsellers()
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