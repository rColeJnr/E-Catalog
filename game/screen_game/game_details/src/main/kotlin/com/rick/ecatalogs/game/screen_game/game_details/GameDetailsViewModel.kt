package com.rick.ecatalogs.game.screen_game.game_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.ecatalogs.game.data_game.data.repository.GameRepository
import com.rick.ecatalogs.game.data_game.data.repository.UserGameDataRepository
import com.rick.ecatalogs.game.data_game.data.repository.UserGameRepository
import com.rick.ecatalogs.game.data_game.model.GameDetails
import com.rick.ecatalogs.game.data_game.model.UserGameDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface GameDetailsUiState {
    data object Loading : GameDetailsUiState
    data object Error : GameDetailsUiState
    data class Loaded(val game: UserGameDetails) : GameDetailsUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GameDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gameRepository: GameRepository,
    private val userGameRepository: UserGameRepository,
    private val userDataRepository: UserGameDataRepository,
) : ViewModel() {

    /** The nav argument `game` (deep link com.rick.ecs://game_details/{game}). */
    private val gameId: Int = checkNotNull(savedStateHandle[ARG_GAME])

    private sealed interface Fetch {
        data object Loading : Fetch
        data object Failed : Fetch
        data class Done(val details: GameDetails) : Fetch
    }

    private val fetch = MutableStateFlow<Fetch>(Fetch.Loading)

    val uiState: StateFlow<GameDetailsUiState> = fetch
        .flatMapLatest { result ->
            when (result) {
                Fetch.Loading -> flowOf(GameDetailsUiState.Loading)
                Fetch.Failed -> flowOf(GameDetailsUiState.Error)
                is Fetch.Done -> userGameRepository.observeDetails(result.details).map(GameDetailsUiState::Loaded)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GameDetailsUiState.Loading)

    init {
        load()
    }

    fun load() {
        fetch.value = Fetch.Loading
        viewModelScope.launch {
            fetch.value = gameRepository.gameDetails(gameId)?.let(Fetch::Done) ?: Fetch.Failed
        }
    }

    fun setFavorite(isFavorite: Boolean) {
        viewModelScope.launch { userDataRepository.setGameFavorite(gameId, isFavorite) }
    }

    companion object {
        const val ARG_GAME = "game"
    }
}
