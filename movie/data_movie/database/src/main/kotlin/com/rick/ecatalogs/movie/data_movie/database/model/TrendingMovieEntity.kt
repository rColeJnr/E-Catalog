package com.rick.ecatalogs.movie.data_movie.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_movie.TrendingMovie

@Entity(tableName = "trending_movie_table")
data class TrendingMovieEntity(
    @PrimaryKey
    val id: Int,
    val adult: Boolean,
    val title: String,
    val overview: String,
    val image: String,
    val popularity: Double,
    @ColumnInfo("release_date")
    val releaseDate: String,
    @ColumnInfo("vote_average")
    val voteAverage: Double,
    @ColumnInfo("vote_count")
    val voteCount: Int,
    /** Place in the catalog list (the API's order); null for rows kept only as search results or favorites. */
    @ColumnInfo("catalog_position")
    val catalogPosition: Int? = null,
)

fun TrendingMovieEntity.asTrendingMovie() = TrendingMovie(
    id = id,
    adult = adult,
    title = title,
    overview = overview,
    image = image,
    popularity = popularity,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    voteCount = voteCount
)