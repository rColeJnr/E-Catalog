package com.rick.book.screen_book.gutenberg_favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.data_book.repository.gutenberg.UserGutenbergDataRepository
import com.rick.data.data_book.repository.gutenberg.UserGutenbergRepository
import com.rick.data.model_book.FavoriteUiEvents
import com.rick.data.model_book.FavoriteUiState
import com.rick.data.model_book.gutenberg.UserGutenberg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GutenbergFavoriteViewModel @Inject constructor(
    private val userGutenbergDataRepository: UserGutenbergDataRepository,
    userGutenbergBookRepository: UserGutenbergRepository,
) : ViewModel() {

    var shouldDisplayUndoGutenbergFavorite by mutableStateOf(false)

    private var lastRemovedGutenbergFavorite: Int? = null

    val feedGutenbergUiState: StateFlow<FavoriteUiState> =
        userGutenbergBookRepository.observeGutenbergFavorite()
            .map<List<UserGutenberg>, FavoriteUiState>(FavoriteUiState::GutenbergFavorites)
            .onStart { emit(FavoriteUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = FavoriteUiState.Loading
            )

    fun onEvent(event: FavoriteUiEvents) {
        when (event) {
            is FavoriteUiEvents.RemoveGutenbergFavorite -> removeGutenbergFavorite(event.bookId)
            is FavoriteUiEvents.UndoGutenbergFavoriteRemoval -> undoGutenbergFavoriteRemoval()
            is FavoriteUiEvents.ClearUndoState -> clearUndoState()
            else -> {}
        }
    }

    private fun undoGutenbergFavoriteRemoval() {
        viewModelScope.launch(Dispatchers.IO) {
            lastRemovedGutenbergFavorite?.let {
                userGutenbergDataRepository.setGutenbergFavoriteId(it, true)
            }
        }
    }

    private fun removeGutenbergFavorite(favoriteId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            shouldDisplayUndoGutenbergFavorite = true
            lastRemovedGutenbergFavorite = favoriteId
            userGutenbergDataRepository.setGutenbergFavoriteId(favoriteId, false)
        }
    }


    private fun clearUndoState() {
        shouldDisplayUndoGutenbergFavorite = false
        lastRemovedGutenbergFavorite = null
    }

}

private const val TAG = "BookFavoriteViewModel"