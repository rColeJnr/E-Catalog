package com.rick.data.data_book.repository.gutenberg

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.data.data_book.model.asGutenbergEntity
import com.rick.data.database_book.dao.GutenbergDao
import com.rick.data.database_book.dao.GutenbergRemoteKeysDao
import com.rick.data.database_book.model.GutenbergEntity
import com.rick.data.database_book.model.asGutenberg
import com.rick.data.model_book.gutenberg.Gutenberg
import com.rick.data.network_book.GutenbergNetworkDataSource
import com.rick.data.network_book.model.GutenbergNetwork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

internal class GutenbergRepositoryImpl @Inject constructor(
    private val gutenbergDao: GutenbergDao,
    private val keysDao: GutenbergRemoteKeysDao,
    private val network: GutenbergNetworkDataSource
) : GutenbergRepository {
    override fun getGutenberg(): Flow<PagingData<GutenbergEntity>> {
        val pagingSourceFactory = { gutenbergDao.getGutenbergs() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                prefetchDistance = 0,
                initialLoadSize = 1
            ),
            remoteMediator = GutenbergRemoteMediator(
                network = network,
                keysDao = keysDao,
                gutenbergDao = gutenbergDao
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }


    override fun searchGutenberg(query: String): Flow<List<Gutenberg>> = channelFlow {
        try {
            val response = network.searchGutenberg(
                query = query
            ).results

            gutenbergDao.clearGutenbergs(response.map(GutenbergNetwork::id))

            gutenbergDao.upsertGutenberg(response.map(GutenbergNetwork::asGutenbergEntity))

            // appending '%' so we can allow other characters to be before and after the query string
            val dbQuery = "%${query.replace(' ', '%')}%"
            gutenbergDao.getGutenbergWithFilters(
                filterByQuery = true,
                query = dbQuery
            )
                .collectLatest { gutenbergEntities -> send(gutenbergEntities.map(GutenbergEntity::asGutenberg)) }

        } catch (e: IOException) {
            Log.e(TAG, e.localizedMessage ?: "IOException")
        } catch (e: HttpException) {
            Log.e(TAG, e.localizedMessage ?: "HTTPException")
        }
    }

    override fun getGutenbergFavorites(favorites: Set<Int>): Flow<List<Gutenberg>> =
        gutenbergDao.getGutenbergWithFilters(
            filterById = true,
            filterIds = favorites
        )
            .map { gutenbergEntities -> gutenbergEntities.map { it.asGutenberg() } }
}

private const val TAG = "GutenbergRepositoryImpl"