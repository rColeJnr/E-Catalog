package com.rick.screen_movie

import com.rick.data_movie.Movie

sealed class UiAction {
    data class Refresh(val refresh: Boolean): UiAction()
    data class NavigateToDetails(val movie: Movie? = null): UiAction()
}
