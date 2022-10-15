package com.rick.data_anime.model_jikan


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Links(
    @SerializedName("first")
    val first: String,
    @SerializedName("last")
    val last: String,
    @SerializedName("prev")
    val prev: String?,
    @SerializedName("next")
    val next: String
) : Parcelable