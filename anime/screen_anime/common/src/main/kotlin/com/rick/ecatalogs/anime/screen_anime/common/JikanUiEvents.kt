package com.rick.ecatalogs.anime.screen_anime.common

sealed interface JikanUiEvents {
    data class UpdateAnimeFavorite(val id: Int, val isFavorite: Boolean) : JikanUiEvents
    data class UpdateMangaFavorite(val id: Int, val isFavorite: Boolean) : JikanUiEvents
}