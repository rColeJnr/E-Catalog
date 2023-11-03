package com.rick.data_movie.ny_times_deprecated

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Link(
    val url: String
): Parcelable