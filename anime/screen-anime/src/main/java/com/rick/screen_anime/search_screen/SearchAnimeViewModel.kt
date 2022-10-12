package com.rick.screen_anime.search_screen

import androidx.lifecycle.ViewModel
import com.rick.data_anime.JikanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchAnimeViewModel @Inject constructor(
    private val repo: JikanRepository
) : ViewModel() {

    val searchUiAction: (SearchUiAction) -> Unit
    val searchUiState: (SearchUiState) -> Unit

    init {

    }

}
sealed class SearchUiAction {
    data class SearchJikan(val query: String) : SearchUiAction()
}

data class SearchUiState(
    val searchQuery: String? = null
)