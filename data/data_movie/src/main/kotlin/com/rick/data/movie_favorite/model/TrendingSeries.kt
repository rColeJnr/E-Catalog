package com.rick.data.movie_favorite.model

import com.rick.data.database_movie.model.TrendingSeriesEntity
import com.rick.data.network_movie.model.TrendingSeriesNetwork

fun TrendingSeriesNetwork.asTrendingSeriesEntity() = TrendingSeriesEntity(
    adult = adult,
    id = id,
    name = name,
    overview = overview,
    image = image,
    mediaType = mediaType,
    popularity = popularity,
    firstAirDate = firstAirDate,
    voteAverage = voteAverage,
    voteCount = voteCount
)