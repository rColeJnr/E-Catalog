package com.rick.movie.data_movie.data.model

import com.rick.data.database_movie.model.TrendingSeriesEntity
import com.rick.data.database_movie.model.TrendingSeriesRecentSearchQueryEntity
import com.rick.data.model_movie.TmdbRecentSearchQuery
import com.rick.data.model_movie.tmdb.series.MySeries
import com.rick.data.model_movie.tmdb.trending_series.TrendingSeries
import com.rick.movie.data_movie.network.model.SeriesResponse
import com.rick.movie.data_movie.network.model.TrendingSeriesNetwork

fun TrendingSeriesNetwork.asTrendingSeriesEntity() = TrendingSeriesEntity(
    adult = adult,
    id = id,
    name = name,
    overview = overview,
    image = image,
    popularity = popularity,
    firstAirDate = firstAirDate,
    voteAverage = voteAverage,
    voteCount = voteCount
)

fun TrendingSeriesNetwork.asTrendingSeries() = TrendingSeries(
    adult = adult,
    id = id,
    name = name,
    overview = overview,
    image = image,
    popularity = popularity,
    firstAirDate = firstAirDate,
    voteAverage = voteAverage,
    voteCount = voteCount
)

fun SeriesResponse.asMySeries() = MySeries(id,
    adult = adult,
    createdBy = createdBy,
    episodeRuntime = episodeRuntime,
    firstAirDate = firstAirDate,
    genres = genres,
    homepage = homepage,
    inProduction = inProduction,
    lastAirDate = lastAirDate,
    name = name,
    numberOfSeasons = numberOfSeasons,
    numberOfEpisodes = numberOfEpisodes,
    overview = overview,
    popularity = popularity,
    image = image,
    voteAverage = voteAverage,
    similar = similar.results.map { it.asTrendingSeries() })

fun TrendingSeriesRecentSearchQueryEntity.asExternalModel() = TmdbRecentSearchQuery(
    query = query,
    queriedDate = queriedDate,
)