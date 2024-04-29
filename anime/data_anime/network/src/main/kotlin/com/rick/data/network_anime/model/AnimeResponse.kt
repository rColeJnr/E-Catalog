package com.rick.data.network_anime.model

import com.rick.data.model_anime.model_jikan.Pagination
import kotlinx.serialization.Serializable

@Serializable
data class AnimeResponse(
    val pagination: Pagination,
    val data: List<AnimeNetwork>,
)
