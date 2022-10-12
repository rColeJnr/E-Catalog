package com.rick.data_anime.model_anime


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Links(
    @SerializedName("first")
    val first: String,
    @SerializedName("last")
    val last: String,
    @SerializedName("prev")
    val prev: Any?,
    @SerializedName("next")
    val next: String
) : Parcelable