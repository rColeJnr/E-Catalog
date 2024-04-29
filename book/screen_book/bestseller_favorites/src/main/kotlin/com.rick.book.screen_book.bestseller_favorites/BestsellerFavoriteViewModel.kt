package com.rick.book.screen_book.bestseller_favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.data_book.repository.bestseller.UserBestsellerDataRepository
import com.rick.data.data_book.repository.bestseller.UserBestsellerRepository
import com.rick.data.model_book.FavoriteUiEvents
import com.rick.data.model_book.FavoriteUiState
import com.rick.data.model_book.bestseller.UserBestseller
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
class BestsellerFavoriteViewModel @Inject constructor(
    private val userBestsellerDataRepository: UserBestsellerDataRepository,
    userBestsellerBookRepository: UserBestsellerRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    var shouldDisplayUndoBestsellerFavorite by mutableStateOf(false)

    private var lastRemovedBestsellerFavorite: String? = null

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
            is FavoriteUiEvents.RemoveBestsellerFavorite -> removeBestsellerFavorite(event.bookId)
            is FavoriteUiEvents.UndoBestsellerFavoriteRemoval -> undoBestsellerFavoriteRemoval()
            is FavoriteUiEvents.ClearUndoState -> clearUndoState()
            else -> {}
        }
    }

    private fun undoBestsellerFavoriteRemoval() {
        viewModelScope.launch(Dispatchers.IO) {
            lastRemovedBestsellerFavorite?.let {
                userBestsellerDataRepository.setBestsellerFavoriteId(it, true)
            }
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
        shouldDisplayUndoBestsellerFavorite = false
        lastRemovedBestsellerFavorite = null
    }

}

private const val TAG = "BookFavoriteViewModel"