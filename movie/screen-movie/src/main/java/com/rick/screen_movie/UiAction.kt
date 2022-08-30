package com.rick.screen_movie

import com.rick.data_movie.ny_times.Movie

sealed class UiAction {
    data class SearchMovie(val title: String): UiAction()
    data class NavigateToDetails(val movie: Movie? = null): UiAction()
}
