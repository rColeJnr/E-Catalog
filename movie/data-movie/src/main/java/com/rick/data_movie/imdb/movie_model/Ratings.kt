package com.rick.data_movie.imdb.movie_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ratings(
    val errorMessage: String,
    val filmAffinity: String,
    val fullTitle: String,
    val imDb: String,
    val imDbId: String,
    val metacritic: String,
    val rottenTomatoes: String,
    val theMovieDb: String,
    val title: String,
    val type: String,
    val year: String
): Parcelable