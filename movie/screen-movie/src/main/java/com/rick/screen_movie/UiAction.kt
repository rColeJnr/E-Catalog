package com.rick.screen_movie

import com.rick.data_movie.tmdb.trending_movie.TrendingMovie

sealed class UiAction {
    data class InsertFavorite(val fav: TrendingMovie): UiAction()
    object RemoveFavorite: UiAction()
}
