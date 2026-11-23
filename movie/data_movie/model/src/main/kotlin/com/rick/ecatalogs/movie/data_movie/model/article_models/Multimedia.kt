package com.rick.ecatalogs.movie.data_movie.model.article_models

import kotlinx.serialization.Serializable

@Serializable
data class Multimedia(
    val default: DefaultImage?
)//: Parcelable

@Serializable
data class DefaultImage(
    val url: String?
)