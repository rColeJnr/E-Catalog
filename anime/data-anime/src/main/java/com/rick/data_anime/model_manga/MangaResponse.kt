package com.rick.data_anime.model_manga


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class MangaResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("pagination")
    val pagination: Pagination
) : Parcelable