package com.rick.screen_movie

import com.rick.data_movie.ResultDto

sealed class UiAction {
    sealed class Scroll: UiAction()
//    data class navigateToDetails(val movie: ResultDto): UiAction()
}
