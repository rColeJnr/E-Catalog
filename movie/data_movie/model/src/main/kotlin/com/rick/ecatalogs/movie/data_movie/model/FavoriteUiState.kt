package com.rick.ecatalogs.movie.data_movie.model

sealed interface FavoriteUiState {
    data object Loading : FavoriteUiState
    data class TrendingMoviesFavorites(val favorites: List<UserTrendingMovie>) : FavoriteUiState
    data class TrendingSeriesFavorites(val favorites: List<UserTrendingSeries>) : FavoriteUiState
    data class ArticlesFavorites(val favorites: List<UserArticle>) : FavoriteUiState
}
