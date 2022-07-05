package com.rick.screen_movie

import com.rick.data_movie.Result

sealed class UiAction {
    sealed class Refresh: UiAction()
    data class NavigateToDetails(val movie: Result): UiAction()
}
