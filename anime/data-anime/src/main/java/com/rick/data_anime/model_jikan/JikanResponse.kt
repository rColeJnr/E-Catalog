package com.rick.data_anime.model_jikan


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class AnimeResponseDto(
    @SerializedName("pagination")
    val pagination: Pagination,
    @SerializedName("data")
    val data: List<JikanDto>,
    @SerializedName("links")
    val links: Links,
    @SerializedName("meta")
    val meta: Meta
)

@Parcelize
data class AnimeResponse(
    @SerializedName("pagination")
    val pagination: Pagination,
    @SerializedName("data")
    val data: List<Jikan>,
) : Parcelable