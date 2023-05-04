package com.rick.screen_movie

import com.rick.data_movie.favorite.Favorite
import com.rick.data_movie.ny_times.Movie

sealed class UiAction {
    data class NavigateToDetails(val movie: Movie? = null): UiAction()
    data class InsertFavorite(val fav: Favorite): UiAction()
    data class RemoveFavorite(val fav: Favorite): UiAction()
}
