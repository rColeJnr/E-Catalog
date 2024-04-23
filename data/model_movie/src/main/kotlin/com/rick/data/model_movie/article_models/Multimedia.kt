package com.rick.data.model_movie.article_models

import kotlinx.serialization.Serializable

@Serializable
data class Multimedia(
    val type: String,
    val url: String,
)//: Parcelable