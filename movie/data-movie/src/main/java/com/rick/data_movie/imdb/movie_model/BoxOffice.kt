package com.rick.data_movie.imdb.movie_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BoxOffice(
    val budget: String,
    val cumulativeWorldwideGross: String,
    val grossUSA: String,
    val openingWeekendUSA: String
): Parcelable