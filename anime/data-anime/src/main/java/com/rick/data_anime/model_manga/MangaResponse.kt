package com.rick.data_anime.model_manga


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class MangaResponseDto(
    @SerializedName("data")
    val manga: List<MangaDto>,
    @SerializedName("pagination")
    val pagination: Pagination
)

@Parcelize
data class MangaResponse(
    @SerializedName("data")
    val manga: List<Manga>,
    @SerializedName("pagination")
    val pagination: Pagination
) : Parcelable