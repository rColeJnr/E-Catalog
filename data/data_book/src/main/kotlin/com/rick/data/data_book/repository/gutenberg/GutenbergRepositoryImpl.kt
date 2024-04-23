package com.rick.data.data_book.repository.gutenberg

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.data.database_book.dao.GutenbergDao
import com.rick.data.database_book.dao.GutenbergRemoteKeysDao
import com.rick.data.database_book.model.GutenbergEntity
import com.rick.data.database_book.model.asGutenberg
import com.rick.data.model_book.gutenberg.Gutenberg
import com.rick.data.network_book.GutenbergNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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


    override fun searchGutenberg(): Flow<List<Gutenberg>> {
        TODO("Not yet implemented")
    }

    override fun getGutenbergFavorites(favorites: Set<Int>): Flow<List<Gutenberg>> =
        gutenbergDao.getGutenbergWithFilters(favorites)
            .map { gutenbergEntities -> gutenbergEntities.map { it.asGutenberg() } }
}