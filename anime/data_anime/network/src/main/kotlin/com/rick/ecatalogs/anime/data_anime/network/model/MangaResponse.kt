package com.rick.ecatalogs.anime.data_anime.network.model

import com.rick.ecatalogs.anime.data_anime.model.model_jikan.Pagination
import kotlinx.serialization.Serializable

@Serializable
data class MangaResponse(
    val pagination: Pagination,
    val data: List<MangaNetwork>,
)