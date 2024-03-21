package com.rick.data_anime.model_jikan


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Link(
    @SerializedName("url")
    val url: String?,
    @SerializedName("label")
    val label: String,
    @SerializedName("active")
    val active: Boolean
) : Parcelable