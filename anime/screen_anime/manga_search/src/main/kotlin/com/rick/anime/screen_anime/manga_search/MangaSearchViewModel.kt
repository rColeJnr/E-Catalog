package com.rick.anime.screen_anime.manga_search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.anime.data_anime.data.repository.manga.CompositeMangaRepository
import com.rick.anime.data_anime.data.repository.manga.MangaRecentSearchRepository
import com.rick.anime.data_anime.data.repository.manga.UserMangaDataRepository
import com.rick.data.domain_anime.GetMangaRecentSearchQueriesUseCase
import com.rick.data.model_anime.MangaRecentSearchQuery
import com.rick.data.model_anime.UserManga
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
// TODO
@HiltViewModel
class MangaSearchViewModel @Inject constructor(
    getMangaRecentSearchQueriesUseCase: GetMangaRecentSearchQueriesUseCase,
    private val mangaRecentSearchRepository: MangaRecentSearchRepository,
    private val compositeMangaRepository: CompositeMangaRepository,
    private val userMangaDataRepository: UserMangaDataRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    val searchState: StateFlow<MangaSearchUiState>
    val recentSearchState: StateFlow<MangaRecentSearchQueriesUiState>

    init {

        searchState = searchQuery.flatMapLatest { query ->
            if (query.length < SEARCH_QUERY_MIN_LENGTH) {
                flowOf(MangaSearchUiState.EmptyQuery)
            } else {
                compositeMangaRepository.searchManga(query)
                    // Not using .asResult() here, because it emits Loading state every
                    // time the user types a letter in the search box, which flickers the screen.
                    .map<List<UserManga>, MangaSearchUiState> { mangas ->
                        MangaSearchUiState.Success(
                            mangas = mangas
                        )
                    }
                    .catch { emit(MangaSearchUiState.Error) }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MangaSearchUiState.EmptyQuery,
        )

        recentSearchState = getMangaRecentSearchQueriesUseCase()
            .map(MangaRecentSearchQueriesUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MangaRecentSearchQueriesUiState.Loading,
            )
    }

    fun onEvent(event: MangaSearchUiEvent) {
        when (event) {
            is MangaSearchUiEvent.SearchQuery -> searchQuery(event.query)
            is MangaSearchUiEvent.UpdateFavorite -> updateFavorite(
                event.id,
                event.isFavorite
            )

            is MangaSearchUiEvent.OnSearchTriggered -> onSearchTriggered(event.query)
            MangaSearchUiEvent.ClearRecentSearches -> clearRecentSearches()
        }
    }

    private fun searchQuery(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    private fun updateFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            userMangaDataRepository.setMangaFavoriteId(id, isFavorite)
        }
    }

    private fun onSearchTriggered(query: String) {
        viewModelScope.launch {
            mangaRecentSearchRepository.insertOrReplaceMangaRecentSearch(searchQuery = query)
        }
    }

    private fun clearRecentSearches() {
        viewModelScope.launch {
            mangaRecentSearchRepository.clearMangaRecentSearches()
        }
    }

}

sealed interface MangaSearchUiState {
    data object EmptyQuery : MangaSearchUiState
    data class Success(val mangas: List<UserManga>) : MangaSearchUiState
    data object Loading : MangaSearchUiState
    data object Error : MangaSearchUiState
}

sealed interface MangaSearchUiEvent {
    data class SearchQuery(val query: String) : MangaSearchUiEvent

    data class UpdateFavorite(val id: Int, val isFavorite: Boolean) : MangaSearchUiEvent

    data class OnSearchTriggered(val query: String) : MangaSearchUiEvent

    data object ClearRecentSearches : MangaSearchUiEvent
}

sealed interface MangaRecentSearchQueriesUiState {
    data object Loading : MangaRecentSearchQueriesUiState

    data class Success(
        val recentQueries: List<MangaRecentSearchQuery> = emptyList(),
    ) : MangaRecentSearchQueriesUiState
}


private const val SEARCH_QUERY = "mangaSearchQuery"
private const val TAG = "mangaSearchViewModel"
private const val SEARCH_QUERY_MIN_LENGTH = 2