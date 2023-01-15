package com.rick.data_anime.model_jikan


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Jpg(
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("small_image_url")
    val smallImageUrl: String,
    @SerializedName("large_image_url")
    val largeImageUrl: String
) : Parcelable