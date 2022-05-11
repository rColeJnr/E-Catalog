package com.rick.moviecatalog.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Result(
    val title: String,
    val summary: String,
    val rating: String,
    val openingDate: String?,
    val link: Link,
    val multimedia: Multimedia,
): Parcelable
