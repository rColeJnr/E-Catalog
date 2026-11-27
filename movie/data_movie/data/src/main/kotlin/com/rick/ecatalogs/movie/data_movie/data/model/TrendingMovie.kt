package com.rick.ecatalogs.movie.data_movie.data.model

import com.rick.ecatalogs.movie.data_movie.database.model.TrendingMovieEntity
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingMovieRecentSearchQueryEntity
import com.rick.ecatalogs.movie.data_movie.model.TmdbRecentSearchQuery
import com.rick.ecatalogs.movie.data_movie.model.tmdb.movie.MyMovie
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_movie.TrendingMovie
import com.rick.ecatalogs.movie.data_movie.network.model.MovieResponse
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingMovieNetwork

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
    similar = similar.results.map { it.asTrendingMovie() },
    extras = tmdbDetailsExtras(
        credits = credits,
        videos = videos,
        images = images,
        watchProviders = watchProviders,
        ageRatings = releaseDates.asAgeRatings(),
    ),
)

fun TrendingMovieRecentSearchQueryEntity.asExternalModel() = TmdbRecentSearchQuery(
    query = query,
    queriedDate = queriedDate,
)