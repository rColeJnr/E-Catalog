package com.rick.data_movie

private const val PAGE_SIZE = 20


//
//    private val dao = db.moviesDao
//    suspend fun getMovieCatalog(offset: Int): Flow<Resource<MovieCatalog>> {
//        return flow {
//            emit(Resource.Loading(true))
//
//            val localMovieCatalog = dao.getMovies()
//
//            if (localMovieCatalog.isNotEmpty()) {
//                emit(
//                    Resource.Success(
//                        data = localMovieCatalog.map { it.toMovieCatalog() }.first()
//                    )
//                )
//                emit(Resource.Loading(false))
//            }
//            try {
//                val remoteMovieCatalog = api.fetchMovieCatalog(offset, QUERY_ORDER)
//
//                dao.clearMovies()
//                dao.insertMovies(remoteMovieCatalog.toMovieCatalogEntity())
//
//                emit(
//                    Resource.Success(
//                        data = dao.getMovies().map { it.toMovieCatalog() }.first()
//                    )
//                )
//            } catch (e: IOException) {
//                e.printStackTrace()
//                emit(Resource.Error(e.message))
//                emit(Resource.Loading(false))
//            } catch (e: HttpException) {
//                e.printStackTrace()
//                emit(Resource.Error(e.message))
//                emit(Resource.Loading(false))
//            }
//        }
//    }