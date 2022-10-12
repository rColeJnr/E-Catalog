package com.rick.data_anime.model_manga


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Prop(
    @SerializedName("from")
    val from: From,
    @SerializedName("to")
    val to: To,
    @SerializedName("string")
    val string: String
) : Parcelable