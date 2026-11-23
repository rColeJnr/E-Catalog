package com.rick.ecatalogs.movie.data_movie.data.model

import com.rick.ecatalogs.movie.data_movie.database.model.TrendingSeriesEntity
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingSeriesRecentSearchQueryEntity
import com.rick.ecatalogs.movie.data_movie.model.TmdbRecentSearchQuery
import com.rick.ecatalogs.movie.data_movie.model.tmdb.series.MySeries
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_series.TrendingSeries
import com.rick.ecatalogs.movie.data_movie.network.model.SeriesResponse
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingSeriesNetwork

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