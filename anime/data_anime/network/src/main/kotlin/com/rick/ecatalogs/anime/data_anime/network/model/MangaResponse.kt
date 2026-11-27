package com.rick.ecatalogs.anime.data_anime.network.model

import kotlinx.serialization.Serializable

@Serializable
data class MangaResponse(
    val data: List<MangaNetwork> = emptyList(),
    val meta: EdgeMeta? = null,
)
