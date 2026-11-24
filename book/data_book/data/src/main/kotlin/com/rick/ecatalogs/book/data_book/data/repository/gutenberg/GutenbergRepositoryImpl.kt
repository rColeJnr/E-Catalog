package com.rick.ecatalogs.book.data_book.data.repository.gutenberg

import com.rick.ecatalogs.book.data_book.model.gutenberg.GutenbergFilter
import com.rick.ecatalogs.data.datastore.ListCache
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.ecatalogs.book.data_book.network.GutenbergNetworkDataSource
import com.rick.ecatalogs.book.data_book.network.model.GutenbergNetwork
import com.rick.ecatalogs.book.data_book.data.model.asGutenbergEntity
import com.rick.ecatalogs.book.data_book.database.BookTransactionRunner
import com.rick.ecatalogs.book.data_book.database.dao.GutenbergDao
import com.rick.ecatalogs.book.data_book.database.dao.GutenbergRemoteKeysDao
import com.rick.ecatalogs.book.data_book.database.model.GutenbergEntity
import com.rick.ecatalogs.book.data_book.database.model.asGutenberg
import com.rick.ecatalogs.book.data_book.model.gutenberg.Gutenberg
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
    private val network: GutenbergNetworkDataSource,
    private val transactionRunner: BookTransactionRunner,
    private val listCache: ListCache,
) : GutenbergRepository {
    override fun getGutenberg(filter: GutenbergFilter): Flow<PagingData<GutenbergEntity>> {
        val pagingSourceFactory = { gutenbergDao.getGutenbergs() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                // Prefetch a page ahead and start with a few pages, so scrolling doesn't keep
                // hitting the end of the loaded data (each load invalidates and rebinds the list)
                prefetchDistance = 20,
                initialLoadSize = 60
            ),
            remoteMediator = GutenbergRemoteMediator(
                network = network,
                keysDao = keysDao,
                gutenbergDao = gutenbergDao,
                transactionRunner = transactionRunner,
                listCache = listCache,
                filter = filter,
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