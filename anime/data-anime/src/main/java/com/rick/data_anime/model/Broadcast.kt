package com.rick.data_anime.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Broadcast(
    @SerializedName("day")
    val day: String?,
    @SerializedName("time")
    val time: String?,
    @SerializedName("timezone")
    val timezone: String?,
    @SerializedName("string")
    val string: String?
) : Parcelable