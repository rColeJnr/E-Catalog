package com.rick.data.model_anime.model_jikan

import kotlinx.serialization.Serializable

@Serializable
data class Trailer(
    val url: String? = null,
    val embed_url: String? = null,
    val images: ImagesTrailer
)