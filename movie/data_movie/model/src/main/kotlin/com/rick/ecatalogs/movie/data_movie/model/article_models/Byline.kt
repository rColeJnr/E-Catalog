package com.rick.ecatalogs.movie.data_movie.model.article_models

import kotlinx.serialization.Serializable

//@Parcelize
@Serializable
data class Byline(
    val original: String
)//: Parcelable