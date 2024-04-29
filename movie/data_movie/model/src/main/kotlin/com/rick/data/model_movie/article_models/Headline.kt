package com.rick.data.model_movie.article_models

import kotlinx.serialization.Serializable


//@Parcelize
@Serializable
data class Headline(
    val main: String? = null,
)//: Parcelable
