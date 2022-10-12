package com.rick.data_anime.model_manga


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Jpg(
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("small_image_url")
    val smallImageUrl: String,
    @SerializedName("large_image_url")
    val largeImageUrl: String
) : Parcelable