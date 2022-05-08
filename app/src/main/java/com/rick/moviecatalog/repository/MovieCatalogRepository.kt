package com.rick.moviecatalog.repository

import android.content.Context
import com.rick.moviecatalog.R
import com.rick.moviecatalog.data.local.MovieCatalogDatabase
import com.rick.moviecatalog.data.model.MovieCatalog
import com.rick.moviecatalog.data.remote.MovieCatalogApi
import com.rick.moviecatalog.data.util.toMovieCatalog
import com.rick.moviecatalog.data.util.toMovieCatalogEntity
import com.rick.moviecatalog.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MovieCatalogRepository @Inject constructor(
    private val api: MovieCatalogApi,
    db: MovieCatalogDatabase,
    private val context: Context
) {

    private val dao = db.dao

    suspend fun getMovieCatalog(offset: Int): Flow<Resource<MovieCatalog>> {
        return flow {
            emit(Resource.Loading(true))

            val localMovieCatalog = dao.getMovieCatalog()

            if (!localMovieCatalog.isNullOrEmpty()) {
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
                emit(Resource.Error(e.message ?: context.getString(R.string.error_message)))
                emit(Resource.Loading(false))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error(e.message ?: context.getString(R.string.error_message)))
                emit(Resource.Loading(false))
            }
        }
    }
}

private const val QUERY_ORDER = "by-publication-date"