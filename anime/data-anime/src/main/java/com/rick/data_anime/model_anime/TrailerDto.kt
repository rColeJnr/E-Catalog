package com.rick.data_anime.model_anime


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class TrailerDto(
    @SerializedName("youtube_id")
    val youtubeId: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("embed_url")
    val embedUrl: String?,
    @SerializedName("images")
    val images: ImagesX
)
@Parcelize
data class Trailer(
    @SerializedName("url")
    val url: String?
) : Parcelable