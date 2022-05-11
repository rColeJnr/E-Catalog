package com.rick.moviecatalog.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Multimedia(
    val src: String
): Parcelable
