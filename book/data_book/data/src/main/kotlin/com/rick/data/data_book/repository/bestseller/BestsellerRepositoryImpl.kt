package com.rick.data.data_book.repository.bestseller

import android.util.Log
import com.rick.data.data_book.model.asBestsellerEntity
import com.rick.data.database_book.dao.BestsellerDao
import com.rick.data.database_book.model.BestsellerEntity
import com.rick.data.database_book.model.asBestseller
import com.rick.data.model_book.bestseller.Bestseller
import com.rick.data.network_book.BestsellerNetworkDataSource
import com.rick.data.network_book.model.BestsellerNetwork
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
//    private val apiKey: String,
//    private val
) : BestsellerRepository {

    override fun getBestseller(
        bookGenre: String, apiKey: String, date: String
    ): Flow<List<Bestseller>> = channelFlow {

        try {
            val bestsellers = network.fetchBestsellers(
                date = date,
                bookGenre = bookGenre,
                apiKey = apiKey
            ).results.books

            bestsellerDao.clearBestsellers(bestsellers.map(BestsellerNetwork::id))

            bestsellerDao.upsertBestsellers(bestsellers.map(BestsellerNetwork::asBestsellerEntity))

            bestsellerDao.getBestsellers()
                .collectLatest { bestsellerEntities -> send(bestsellerEntities.map(BestsellerEntity::asBestseller)) }
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