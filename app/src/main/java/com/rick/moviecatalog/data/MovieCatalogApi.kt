package com.rick.moviecatalog.data

import com.rick.moviecatalog.data.remote.MovieCatalogDto
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface MovieCatalogApi {

    @GET
    suspend fun fetchMovies(): Flow<List<MovieCatalogDto>>
}