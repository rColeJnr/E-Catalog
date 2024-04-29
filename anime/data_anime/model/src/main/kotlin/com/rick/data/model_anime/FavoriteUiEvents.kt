package com.rick.data.model_anime

sealed class FavoriteUiEvents {
    data class RemoveAnimeFavorite(val animeId: Int) : FavoriteUiEvents()
    data class RemoveMangaFavorite(val mangaId: Int) : FavoriteUiEvents()
    data object UndoAnimeFavoriteRemoval : FavoriteUiEvents()
    data object UndoMangaFavoriteRemoval : FavoriteUiEvents()
    data object ClearUndoState : FavoriteUiEvents()
    data class ShouldShowAnime(val show: Boolean) : FavoriteUiEvents()
    data class ShouldShowManga(val show: Boolean) : FavoriteUiEvents()
}