package com.rick.data_anime.model_manga


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ImagesDto(
    @SerializedName("jpg")
    val jpg: Jpg,
    @SerializedName("webp")
    val webp: Webp
)
@Parcelize
data class Images(
    @SerializedName("jpg")
    val jpg: Jpg
) : Parcelable