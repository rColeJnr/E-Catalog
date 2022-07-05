package com.rick.screen_movie

import com.rick.data_movie.Result

sealed class UiAction {
    data class navigateToDetails(val movie: Result): UiAction()
}
