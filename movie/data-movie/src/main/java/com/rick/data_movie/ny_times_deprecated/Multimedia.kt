package com.rick.data_movie.ny_times_deprecated

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Deprecated("Use ny_times/Multimedia instead")
@Parcelize
data class Multimedia(
    val src: String
): Parcelable
