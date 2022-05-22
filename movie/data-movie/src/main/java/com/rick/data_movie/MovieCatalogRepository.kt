package com.rick.data_movie

import com.rick.core.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MovieCatalogRepository @Inject constructor(
    private val api: MovieCatalogApi,
    db: MovieCatalogDatabase
) {

    private val dao = db.dao

    suspend fun getMovieCatalog(offset: Int): Flow<Resource<MovieCatalog>> {
        return flow {
            emit(Resource.Loading(true))

            val localMovieCatalog = dao.getMovieCatalog()

            if (localMovieCatalog.isNotEmpty()) {
                emit(
                    Resource.Success(
                        data = localMovieCatalog.map { it.toMovieCatalog() }.first()
                    )
                )
                emit(Resource.Loading(false))
            }
            try {
                val remoteMovieCatalog = api.fetchMovieCatalog(offset, QUERY_ORDER)

                dao.clearMovieCatalogEntities()
                dao.insertMovieCatalog(remoteMovieCatalog.toMovieCatalogEntity())

                emit(
                    Resource.Success(
                        data = dao.getMovieCatalog().map { it.toMovieCatalog() }.first()
                    )
                )
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
            }
        }
    }
}

private const val QUERY_ORDER = "by-publication-date"