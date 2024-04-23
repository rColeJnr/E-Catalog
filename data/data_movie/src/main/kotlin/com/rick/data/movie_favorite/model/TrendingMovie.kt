package com.rick.data.movie_favorite.model

import com.rick.data.database_movie.model.TrendingMovieEntity
import com.rick.data.network_movie.model.TrendingMovieNetwork

fun TrendingMovieNetwork.asTrendingMovieEntity() = TrendingMovieEntity(
    id = id,
    adult = adult,
    image = image,
    title = title,
    overview = overview,
    mediaType = mediaType,
    popularity = popularity,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    voteCount = voteCount
)