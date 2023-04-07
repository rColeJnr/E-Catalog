package com.rick.data_anime

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.rick.core.Resource
import com.rick.data_anime.favorite.JikanFavorite
import com.rick.data_anime.model_jikan.Jikan
import com.rick.data_anime.model_jikan.toJikanResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val PAGE_SIZE = 2

class JikanRepository @Inject constructor(
    private val api: JikanApi,
    private val db: JikanDatabase
) {

    fun fetchAnime(): Flow<PagingData<Jikan>> {
        val pagingSourceFactory = { db.jikanDao.getAnime() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true,
                prefetchDistance = 1,
                initialLoadSize = 1
            ),
            remoteMediator = AnimeRemoteMediator(api, db),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun fetchManga(): Flow<PagingData<Jikan>> {
        val pagingSourceFactory = { db.jikanDao.getManga() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 1,
                enablePlaceholders = true,
                prefetchDistance = 1,
                initialLoadSize = 1
            ),
            remoteMediator = MangaRemoteMediator(api, db),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun searchAnime(query: String): Flow<Resource<List<Jikan>>> {
        var animes: List<Jikan> = listOf()
        // appending '%' so we can allow other characters to be before and after the query string
        val dbQuery = "%${query.replace(' ', '%')}%"
        return flow {

            emit(Resource.Loading(true))

            db.withTransaction {
                animes = db.jikanDao.searchAnimeAndManga(dbQuery)
            }

            if (animes.isNotEmpty()) {
                emit(Resource.Success<List<Jikan>>(data = animes))
                emit(Resource.Loading(false))
            }

            try {
                val response = api.searchAnime(query).toJikanResponse()
                if (response.data.isNotEmpty()) {
                    db.withTransaction {
                        db.jikanDao.insertJikan(response.data)
                        animes = db.jikanDao.searchAnimeAndManga(dbQuery)
                    }
                    emit(Resource.Success<List<Jikan>>(data = animes))
                    emit(Resource.Loading(false))
                } else {
                    emit(Resource.Error(message = null))
                    emit(Resource.Loading(false))
                }
            } catch (e: IOException) {
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
            } catch (e: HttpException) {
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
            }
        }

    }

    fun searchManga(query: String): Flow<Resource<List<Jikan>>> {
        var mangas: List<Jikan> = listOf()
        // appending '%' so we can allow other characters to be before and after the query string
        val dbQuery = "%${query.replace(' ', '%')}%"
        return flow {

            emit(Resource.Loading(true))

            db.withTransaction {
                mangas = db.jikanDao.searchAnimeAndManga(dbQuery)
            }

            if (mangas.isNotEmpty()) {
                emit(Resource.Success<List<Jikan>>(data = mangas))
                emit(Resource.Loading(false))
            }

            try {
                val response = api.searchManga(query).toJikanResponse()
                if (response.data.isNotEmpty()) {
                    db.withTransaction {
                        db.jikanDao.insertJikan(response.data)
                        mangas = db.jikanDao.searchAnimeAndManga(dbQuery)
                    }
                    emit(Resource.Success<List<Jikan>>(data = mangas))
                    emit(Resource.Loading(false))
                } else {
                    emit(Resource.Error(message = null))
                    emit(Resource.Loading(false))
                }
            } catch (e: IOException) {
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
            } catch (e: HttpException) {
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
            }
        }

    }

    suspend fun getFavoriteAnime(): Flow<Resource<List<JikanFavorite>>> {
        return flow {

            emit(Resource.Loading(true))

            val favorites: List<JikanFavorite> = db.favDao.getFavoriteAnime()

            emit(Resource.Loading(false))
            emit(Resource.Success(favorites))

        }
    }

    suspend fun getFavoriteManga(): Flow<Resource<List<JikanFavorite>>> {
        return flow {

            emit(Resource.Loading(true))

            val favorites: List<JikanFavorite> = db.favDao.getFavoriteManga()

            emit(Resource.Loading(false))
            emit(Resource.Success(favorites))

        }
    }

    suspend fun insertFavorite(favorite: JikanFavorite) {
        db.favDao.insert(jikan = favorite)
    }

    suspend fun deleteFavorite(favorite: JikanFavorite) {
        db.favDao.delete(favorite)
    }

}