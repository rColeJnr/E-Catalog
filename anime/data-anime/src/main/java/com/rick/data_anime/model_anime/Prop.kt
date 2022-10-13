package com.rick.data_anime.model_anime


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Prop(
    @SerializedName("from")
    val from: From,
    @SerializedName("to")
    val to: To?
) : Parcelable