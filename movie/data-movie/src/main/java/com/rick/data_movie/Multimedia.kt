package com.rick.data_movie

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Multimedia(
    val src: String
): Parcelable
