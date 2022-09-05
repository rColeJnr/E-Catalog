package com.rick.data_movie.imdb.movie_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Images(
    val id: String,
    val items: List<Image>,
    val errorMessage: String
): Parcelable

@Parcelize
data class Image (
    val title: String,
    val image: String
): Parcelable
