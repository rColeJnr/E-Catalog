package com.rick.data_anime.model_anime


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class AiredDto(
    @SerializedName("from")
    val from: String,
    @SerializedName("to")
    val to: String?,
    @SerializedName("prop")
    val prop: Prop,
    @SerializedName("string")
    val string: String
)
@Parcelize
data class Aired(
    @SerializedName("prop")
    val prop: Prop,
    @SerializedName("string")
    val string: String
) : Parcelable