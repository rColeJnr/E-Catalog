package com.rick.data_anime.model_jikan


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class PublishedDto(
    @SerializedName("prop")
    val prop: Prop,
    @SerializedName("string")
    val string: String,
)
@Parcelize
data class Published(
    @SerializedName("prop")
    val prop: Prop,
    @SerializedName("string")
    val string: String,
) : Parcelable