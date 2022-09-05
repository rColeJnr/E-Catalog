package com.rick.data_movie.imdb.movie_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Similar(
    val id: String,
    val title: String,
    val image: String,
    val imDbRating: String,
): Parcelable