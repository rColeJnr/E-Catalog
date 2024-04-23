package com.rick.data.data_book.repository.bestseller

import com.rick.data.data_book.model.asBestsellerEntity
import com.rick.data.database_book.dao.BestsellerDao
import com.rick.data.database_book.model.asBestseller
import com.rick.data.model_book.bestseller.Bestseller
import com.rick.data.network_book.BestsellerNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
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

        val bestsellers = network.fetchBestsellers(
            date = date,
            bookGenre = bookGenre,
            apiKey = apiKey
        ).results.books
        bestsellerDao.clearBestsellers()

        bestsellerDao.upsertBestsellers(bestsellers.map { it.asBestsellerEntity() })

        bestsellerDao.getBestsellers()
            .collectLatest { bestsellerEntities -> send(bestsellerEntities.map { it.asBestseller() }) }

    }

    override fun getBestsellerFavorites(favorites: Set<String>): Flow<List<Bestseller>> =
        bestsellerDao.getBestsellerWithFilters(favorites)
            .map { bestsellerEntities -> bestsellerEntities.map { it.asBestseller() } }
}