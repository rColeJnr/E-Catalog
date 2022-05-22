package com.rick.data_movie

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Link(
    val url: String
): Parcelable