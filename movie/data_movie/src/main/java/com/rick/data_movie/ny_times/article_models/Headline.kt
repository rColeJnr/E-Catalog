package com.rick.data_movie.ny_times.article_models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Headline(
    @SerializedName("main")
    val main: String,
): Parcelable
