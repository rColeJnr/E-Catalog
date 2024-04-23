package com.rick.screen_book.favorite_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.data_book.repository.bestseller.UserBestsellerDataRepository
import com.rick.data.data_book.repository.bestseller.UserBestsellerRepository
import com.rick.data.data_book.repository.gutenberg.UserGutenbergDataRepository
import com.rick.data.data_book.repository.gutenberg.UserGutenbergRepository
import com.rick.data.model_book.FavoriteUiEvents
import com.rick.data.model_book.FavoriteUiState
import com.rick.data.model_book.bestseller.UserBestseller
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
class BookFavoriteViewModel @Inject constructor(
    private val userBestsellerDataRepository: UserBestsellerDataRepository,
    private val userGutenbergDataRepository: UserGutenbergDataRepository,
    userBestsellerBookRepository: UserBestsellerRepository,
    userGutenbergBookRepository: UserGutenbergRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val showBestsellers = savedStateHandle.getStateFlow(key = SHOW_BESTSELLERS, false)
    val showGutenberg = savedStateHandle.getStateFlow(key = SHOW_GUTENBERG, true)

    var shouldDisplayUndoGutenbergFavorite by mutableStateOf(false)
    var shouldDisplayUndoBestsellerFavorite by mutableStateOf(false)

    private var lastRemovedBestsellerFavorite: String? = null
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

    val feedBestsellerUiState: StateFlow<FavoriteUiState> =
        userBestsellerBookRepository.observeBestsellerFavorite()
            .map<List<UserBestseller>, FavoriteUiState>(FavoriteUiState::BestsellerFavorites)
            .onStart { emit(FavoriteUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = FavoriteUiState.Loading
            )

    fun onEvent(event: FavoriteUiEvents) {
        when (event) {
            is FavoriteUiEvents.RemoveGutenbergFavorite -> removeGutenbergFavorite(event.bookId)
            is FavoriteUiEvents.RemoveBestsellerFavorite -> removeBestsellerFavorite(event.bookId)
            is FavoriteUiEvents.UndoBestsellerFavoriteRemoval -> undoBestsellerFavoriteRemoval()
            is FavoriteUiEvents.UndoGutenbergFavoriteRemoval -> undoGutenbergFavoriteRemoval()
            is FavoriteUiEvents.ClearUndoState -> clearUndoState()
            is FavoriteUiEvents.ShouldShowBestsellers -> shouldShowBestsellers(event.show)
            is FavoriteUiEvents.ShouldShowGutenberg -> shouldShowGutenberg(event.show)
        }
    }

    private fun undoGutenbergFavoriteRemoval() {
        viewModelScope.launch(Dispatchers.IO) {
            lastRemovedGutenbergFavorite?.let {
                userGutenbergDataRepository.setGutenbergFavoriteId(it, true)
            }
        }
    }

    private fun undoBestsellerFavoriteRemoval() {
        viewModelScope.launch(Dispatchers.IO) {
            lastRemovedBestsellerFavorite?.let {
                userBestsellerDataRepository.setBestsellerFavoriteId(it, true)
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

    private fun removeBestsellerFavorite(favoriteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            shouldDisplayUndoBestsellerFavorite = true
            lastRemovedBestsellerFavorite = favoriteId
            userBestsellerDataRepository.setBestsellerFavoriteId(favoriteId, false)
        }
    }

    private fun clearUndoState() {
        shouldDisplayUndoGutenbergFavorite = false
        shouldDisplayUndoBestsellerFavorite = false
        lastRemovedBestsellerFavorite = null
        lastRemovedGutenbergFavorite = null
    }

    private fun shouldShowBestsellers(show: Boolean) {
        savedStateHandle[SHOW_BESTSELLERS] = show
    }

    private fun shouldShowGutenberg(show: Boolean) {
        savedStateHandle[SHOW_GUTENBERG] = show
    }

}

private const val SHOW_BESTSELLERS = "showBestsellers"
private const val SHOW_GUTENBERG = "showGutenberg"
private const val TAG = "BookFavoriteViewModel"