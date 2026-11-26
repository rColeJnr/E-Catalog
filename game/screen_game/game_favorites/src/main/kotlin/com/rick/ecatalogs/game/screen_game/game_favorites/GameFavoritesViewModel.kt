package com.rick.ecatalogs.game.screen_game.game_favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.ecatalogs.game.data_game.data.repository.UserGameDataRepository
import com.rick.ecatalogs.game.data_game.data.repository.UserGameRepository
import com.rick.ecatalogs.game.data_game.model.UserGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface GameFavoritesUiState {
    data object Loading : GameFavoritesUiState
    data class Loaded(val games: List<UserGame>) : GameFavoritesUiState
}

@HiltViewModel
class GameFavoritesViewModel @Inject constructor(
    userGameRepository: UserGameRepository,
    private val userDataRepository: UserGameDataRepository,
) : ViewModel() {

    val uiState: StateFlow<GameFavoritesUiState> = userGameRepository.observeFavoriteGames()
        .map<List<UserGame>, GameFavoritesUiState>(GameFavoritesUiState::Loaded)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GameFavoritesUiState.Loading)

    fun setFavorite(gameId: Int, isFavorite: Boolean) {
        viewModelScope.launch { userDataRepository.setGameFavorite(gameId, isFavorite) }
    }
}
