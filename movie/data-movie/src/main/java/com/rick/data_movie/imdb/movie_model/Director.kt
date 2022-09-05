package com.rick.data_movie.imdb.movie_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Director (
    val id: String,
    val name: String
): Parcelable
