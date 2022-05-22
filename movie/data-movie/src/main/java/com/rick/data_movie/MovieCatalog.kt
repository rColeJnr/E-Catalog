package com.rick.data_movie

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieCatalog(
    val results: List<Result>,
    val hasMore: Boolean
): Parcelable
