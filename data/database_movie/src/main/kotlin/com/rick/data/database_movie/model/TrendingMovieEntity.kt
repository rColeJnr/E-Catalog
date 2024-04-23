package com.rick.data.database_movie.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rick.data.model_movie.tmdb.trending_movie.TrendingMovie

@Entity(tableName = "trending_movie_table")
data class TrendingMovieEntity(
    @PrimaryKey
    val id: Int,
    val adult: Boolean,
    val title: String,
    val overview: String,
    val image: String,
    @ColumnInfo("media_type")
    val mediaType: String,
    val popularity: Double,
    @ColumnInfo("release_date")
    val releaseDate: String,
    @ColumnInfo("vote_average")
    val voteAverage: Double,
    @ColumnInfo("vote_count")
    val voteCount: Int,
)

fun TrendingMovieEntity.asTrendingMovie() = TrendingMovie(
    id = id,
    adult = adult,
    title = title,
    overview = overview,
    image = image,
    mediaType = mediaType,
    popularity = popularity,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    voteCount = voteCount
)