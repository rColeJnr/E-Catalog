package com.rick.data_anime.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class AnimeResponseDto(
    @SerializedName("pagination")
    val pagination: Pagination,
    @SerializedName("data")
    val data: List<DataDto>,
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
    val data: List<DataDto>,
) : Parcelable