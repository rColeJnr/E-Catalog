package com.rick.data_movie.imdb.movie_model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "imdb_movie")
@Parcelize
data class IMDBMovie(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val title: String,
    val type: String,
    val year: String,
    val image: String,
    val releaseDate: String,
    val runtimeStr: String,
    val plot: String,
    val awards: String,
    val actorList: List<Actor>,
    val genres: String,
    val directorList: List<Director>,
    val companies: String,
    val countries: String,
    val languages: String,
    val contentRating: String,
    val ratings: Ratings,
    val images: Images,
    val boxOffice: BoxOffice,
    val similars: List<Similar>,
    val errorMessage: String?
): Parcelable