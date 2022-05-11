package com.rick.moviecatalog.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieCatalog(
    val results: List<Result>,
    val hasMore: Boolean
): Parcelable
