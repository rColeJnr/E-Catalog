package com.rick.data_anime.model_anime


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Title(
    @SerializedName("type")
    val type: String,
    @SerializedName("title")
    val title: String
) : Parcelable