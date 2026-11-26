package com.rick.ecatalogs.game.screen_game.game_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.ecatalogs.game.data_game.data.repository.GameRecentSearchRepository
import com.rick.ecatalogs.game.data_game.data.repository.UserGameDataRepository
import com.rick.ecatalogs.game.data_game.data.repository.UserGameRepository
import com.rick.ecatalogs.game.data_game.model.UserGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface GameSearchResults {
    /** Nothing typed yet: recent searches are shown instead. */
    data object Idle : GameSearchResults
    data object Searching : GameSearchResults
    data class Found(val games: List<UserGame>) : GameSearchResults
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class GameSearchViewModel @Inject constructor(
    private val userGameRepository: UserGameRepository,
    private val userDataRepository: UserGameDataRepository,
    private val recentSearchRepository: GameRecentSearchRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val results: StateFlow<GameSearchResults> = _query
        .map(String::trim)
        .debounce(SEARCH_DEBOUNCE_MILLIS)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.length < MIN_QUERY_LENGTH) flowOf(GameSearchResults.Idle)
            else userGameRepository.observeSearch(query)
                .map<List<UserGame>, GameSearchResults>(GameSearchResults::Found)
                .onStart { emit(GameSearchResults.Searching) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GameSearchResults.Idle)

    val recentSearches: StateFlow<List<String>> = recentSearchRepository.getRecentSearchQueries()
        .map { queries -> queries.map { it.query } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onQueryChange(query: String) {
        _query.value = query
    }

    /** The keyboard's search action, or a tapped recent search. */
    fun onSearchTriggered(query: String) {
        val trimmed = query.trim()
        if (trimmed.length < MIN_QUERY_LENGTH) return
        _query.value = trimmed
        viewModelScope.launch { recentSearchRepository.insertOrReplaceRecentSearch(trimmed) }
    }

    fun clearRecentSearches() {
        viewModelScope.launch { recentSearchRepository.clearRecentSearches() }
    }

    fun setFavorite(gameId: Int, isFavorite: Boolean) {
        viewModelScope.launch { userDataRepository.setGameFavorite(gameId, isFavorite) }
    }
}

private const val SEARCH_DEBOUNCE_MILLIS = 400L
private const val MIN_QUERY_LENGTH = 2
