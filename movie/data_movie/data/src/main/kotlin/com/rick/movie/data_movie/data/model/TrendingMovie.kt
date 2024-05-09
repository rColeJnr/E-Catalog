package com.rick.movie.data_movie.data.model

import com.rick.data.database_movie.model.TrendingMovieEntity
import com.rick.data.database_movie.model.TrendingMovieRecentSearchQueryEntity
import com.rick.data.model_movie.TmdbRecentSearchQuery
import com.rick.data.model_movie.tmdb.movie.MyMovie
import com.rick.data.model_movie.tmdb.trending_movie.TrendingMovie
import com.rick.movie.data_movie.network.model.MovieResponse
import com.rick.movie.data_movie.network.model.TrendingMovieNetwork

fun TrendingMovieNetwork.asTrendingMovieEntity() = TrendingMovieEntity(
    id = id,
    adult = adult,
    image = image ?: "",
    title = title ?: "",
    overview = overview ?: "",
    popularity = popularity ?: 0.0,
    releaseDate = releaseDate ?: "",
    voteAverage = voteAverage ?: 0.0,
    voteCount = voteCount ?: 0,
)

fun TrendingMovieNetwork.asTrendingMovie() = TrendingMovie(
    id = id,
    adult = adult,
    image = image ?: "",
    title = title ?: "",
    overview = overview ?: "",
    popularity = popularity ?: 0.0,
    releaseDate = releaseDate ?: "",
    voteAverage = voteAverage ?: 0.0,
    voteCount = voteCount ?: 0
)

fun MovieResponse.asMyMovie() = MyMovie(
    id = id,
    adult = adult,
    budget = budget,
    genres = genres,
    homepage = homepage,
    overview = overview,
    popularity = popularity,
    image = image,
    releaseDate = releaseDate,
    revenue = revenue,
    runtime = runtime,
    title = title,
    voteAverage = voteAverage,
    voteCount = voteCount,
    similar = similar.results.map { it.asTrendingMovie() }
)

fun TrendingMovieRecentSearchQueryEntity.asExternalModel() = TmdbRecentSearchQuery(
    query = query,
    queriedDate = queriedDate,
)