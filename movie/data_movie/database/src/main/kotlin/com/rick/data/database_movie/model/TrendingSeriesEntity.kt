package com.rick.data.database_movie.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rick.data.model_movie.tmdb.trending_series.TrendingSeries

@Entity(tableName = "trending_series_table")
data class TrendingSeriesEntity(
    @PrimaryKey
    val id: Int,
    val adult: Boolean,
    val name: String,
    val overview: String,
    val image: String,
    val popularity: Double,
    @ColumnInfo("first_air_date")
    val firstAirDate: String,
    @ColumnInfo("vote_average")
    val voteAverage: Double,
    @ColumnInfo("vote_count")
    val voteCount: Int,
)

fun TrendingSeriesEntity.asTrendingSeries() = TrendingSeries(
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
