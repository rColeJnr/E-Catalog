package com.rick.ecatalogs.anime.data_anime.model.model_jikan

import kotlinx.serialization.Serializable

@Serializable
data class Trailer(
    val url: String?,
    val embed_url: String?,
    val images: ImagesTrailer
)