package com.rick.moviecatalog.repository

import com.rick.moviecatalog.data.MovieCatalogApi
import com.rick.moviecatalog.data.model.MovieCatalog
import com.rick.moviecatalog.data.toMovieCatalog
import com.rick.moviecatalog.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class MovieCatalogRepository(
    private val api: MovieCatalogApi
) {

    suspend fun getMovieCatalog(offset: Int): Flow<Resource<MovieCatalog>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val response = api.fetchMovieCatalog(offset, QUERY_ORDER)
                emit(Resource.Success(
                    data = response.toMovieCatalog()
                ))
                emit(Resource.Loading(false))
            } catch (e: IOException){
                e.printStackTrace()
                emit(Resource.Error(e.message ?: ""))
                emit(Resource.Loading(false))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error(e.message ?: ""))
                emit(Resource.Loading(false))
            }
        }
    }
}

private const val QUERY_ORDER = "by-publication-date"