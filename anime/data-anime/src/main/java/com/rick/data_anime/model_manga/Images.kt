package com.rick.data_anime.model_manga


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Images(
    @SerializedName("jpg")
    val jpg: Jpg,
    @SerializedName("webp")
    val webp: Webp
) : Parcelable