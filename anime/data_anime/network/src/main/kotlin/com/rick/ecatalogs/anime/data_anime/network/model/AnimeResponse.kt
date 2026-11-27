package com.rick.ecatalogs.anime.data_anime.network.model

import kotlinx.serialization.Serializable

@Serializable
data class AnimeResponse(
    val data: List<AnimeNetwork> = emptyList(),
    val meta: EdgeMeta? = null,
)
