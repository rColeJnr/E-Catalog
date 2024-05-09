package com.rick.anime.screen_anime.anime_search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.anime.data_anime.data.repository.anime.AnimeRecentSearchRepository
import com.rick.anime.data_anime.data.repository.anime.CompositeAnimeRepository
import com.rick.anime.data_anime.data.repository.anime.UserAnimeDataRepository
import com.rick.data.domain_anime.GetAnimeRecentSearchQueriesUseCase
import com.rick.data.model_anime.AnimeRecentSearchQuery
import com.rick.data.model_anime.UserAnime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// WHY ISNT MY CODEBASE CONSISTENT THROUGHOUT, AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHHHHH
// I'm in public so i can't just blast.

@HiltViewModel
class AnimeSearchViewModel @Inject constructor(
    getAnimeRecentSearchQueriesUseCase: GetAnimeRecentSearchQueriesUseCase,
    private val animeRecentSearchRepository: AnimeRecentSearchRepository,
    private val compositeAnimeRepository: CompositeAnimeRepository,
    private val userAnimeDataRepository: UserAnimeDataRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    val searchState: StateFlow<AnimeSearchUiState>
    val recentSearchState: StateFlow<AnimeRecentSearchQueriesUiState>

    init {

        searchState = searchQuery.flatMapLatest { query ->
            if (query.length < SEARCH_QUERY_MIN_LENGTH) {
                flowOf(AnimeSearchUiState.EmptyQuery)
            } else {
                compositeAnimeRepository.searchAnime(query)
                    // Not using .asResult() here, because it emits Loading state every
                    // time the user types a letter in the search box, which flickers the screen.
                    .map<List<UserAnime>, AnimeSearchUiState> { animes ->
                        AnimeSearchUiState.Success(
                            animes = animes
                        )
                    }
                    .catch { emit(AnimeSearchUiState.Error) }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AnimeSearchUiState.EmptyQuery,
        )

        recentSearchState = getAnimeRecentSearchQueriesUseCase()
            .map(AnimeRecentSearchQueriesUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AnimeRecentSearchQueriesUiState.Loading,
            )
    }

    fun onEvent(event: AnimeSearchUiEvent) {
        when (event) {
            is AnimeSearchUiEvent.SearchQuery -> searchQuery(event.query)
            is AnimeSearchUiEvent.UpdateFavorite -> updateFavorite(
                event.id,
                event.isFavorite
            )

            is AnimeSearchUiEvent.OnSearchTriggered -> onSearchTriggered(event.query)
            AnimeSearchUiEvent.ClearRecentSearches -> clearRecentSearches()
        }
    }

    private fun searchQuery(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    private fun updateFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            userAnimeDataRepository.setAnimeFavoriteId(id, isFavorite)
        }
    }

    private fun onSearchTriggered(query: String) {
        viewModelScope.launch {
            animeRecentSearchRepository.insertOrReplaceAnimeRecentSearch(searchQuery = query)
        }
    }

    private fun clearRecentSearches() {
        viewModelScope.launch {
            animeRecentSearchRepository.clearAnimeRecentSearches()
        }
    }

}

sealed interface AnimeSearchUiState {
    data object EmptyQuery : AnimeSearchUiState
    data class Success(val animes: List<UserAnime>) : AnimeSearchUiState
    data object Loading : AnimeSearchUiState
    data object Error : AnimeSearchUiState
}

sealed interface AnimeSearchUiEvent {
    data class SearchQuery(val query: String) : AnimeSearchUiEvent

    data class UpdateFavorite(val id: Int, val isFavorite: Boolean) : AnimeSearchUiEvent

    data class OnSearchTriggered(val query: String) : AnimeSearchUiEvent

    data object ClearRecentSearches : AnimeSearchUiEvent
}

sealed interface AnimeRecentSearchQueriesUiState {
    data object Loading : AnimeRecentSearchQueriesUiState

    data class Success(
        val recentQueries: List<AnimeRecentSearchQuery> = emptyList(),
    ) : AnimeRecentSearchQueriesUiState
}


private const val SEARCH_QUERY = "animeSearchQuery"
private const val TAG = "animeSearchViewModel"
private const val SEARCH_QUERY_MIN_LENGTH = 2