package com.rick.data_movie

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator

@OptIn(ExperimentalPagingApi::class)
class MovieCatalogRemoteMediator(
    private val api: MovieCatalogApi,
    private val db: MovieCatalogDatabase
): RemoteMediator<Int, MovieCatalogEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieCatalogEntity>
    ): MediatorResult {
        TODO("Not yet implemented")
    }
}