package com.rick.anime.anime_screen.common

sealed interface JikanUiEvents {
    data class UpdateAnimeFavorite(val id: Int, val isFavorite: Boolean) : JikanUiEvents
    data class UpdateMangaFavorite(val id: Int, val isFavorite: Boolean) : JikanUiEvents
}