package com.rick.ecatalogs.game.screen_game.game_catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.ecatalogs.game.data_game.data.repository.GameBrowseRepository
import com.rick.ecatalogs.game.data_game.data.repository.UserGameDataRepository
import com.rick.ecatalogs.game.data_game.data.repository.UserGameRepository
import com.rick.ecatalogs.game.data_game.model.Game
import com.rick.ecatalogs.game.data_game.model.GameGenre
import com.rick.ecatalogs.game.data_game.model.UserGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GameCatalogViewModel @Inject constructor(
    private val userGameRepository: UserGameRepository,
    private val userDataRepository: UserGameDataRepository,
    private val browseRepository: GameBrowseRepository,
) : ViewModel() {

    private val _selectedGenre = MutableStateFlow<Int?>(null)
    /** null: the "popular now" catalog; otherwise the most rated games of that genre. */
    val selectedGenre: StateFlow<Int?> = _selectedGenre.asStateFlow()

    private val popular = userGameRepository.observePopularGames(viewModelScope)

    val games: Flow<PagingData<UserGame>> = _selectedGenre
        .flatMapLatest { genreId ->
            if (genreId == null) popular
            else userGameRepository.withFavorites(browseRepository.gamesByGenre(genreId).cachedIn(viewModelScope))
        }
        .cachedIn(viewModelScope)

    val comingSoon: StateFlow<List<Game>> = browseRepository.comingSoon()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val recentlyReleased: StateFlow<List<Game>> = browseRepository.recentlyReleased()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val genres: StateFlow<List<GameGenre>> = flow { emit(browseRepository.genres()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectGenre(genreId: Int?) {
        _selectedGenre.value = genreId
    }

    fun setFavorite(gameId: Int, isFavorite: Boolean) {
        viewModelScope.launch { userDataRepository.setGameFavorite(gameId, isFavorite) }
    }
}
