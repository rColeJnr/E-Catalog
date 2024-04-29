package com.rick.data.model_movie

sealed class FavoriteUiEvents {
    data class ShouldShowArticles(val show: Boolean) : FavoriteUiEvents()
    data class ShouldShowMovies(val show: Boolean) : FavoriteUiEvents()
    data class ShouldShowSeries(val show: Boolean) : FavoriteUiEvents()
    data class RemoveTrendingMovieFavorite(val movieId: Int) : FavoriteUiEvents()
    data class RemoveTrendingSeriesFavorite(val seriesId: Int) : FavoriteUiEvents()
    data class RemoveArticleFavorite(val articleId: Long) : FavoriteUiEvents()
    data object UndoTrendingMovieFavoriteRemoval : FavoriteUiEvents()
    data object UndoTrendingSeriesFavoriteRemoval : FavoriteUiEvents()
    data object UndoArticleFavoriteRemoval : FavoriteUiEvents()
    data object ClearUndoState : FavoriteUiEvents()
}