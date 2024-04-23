package com.rick.data.model_anime.model_jikan

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Theme(
    @SerializedName("mal_id")
    @SerialName("mal_id")
    val malId: Int,
    val type: String,
    val name: String,
    val url: String
)