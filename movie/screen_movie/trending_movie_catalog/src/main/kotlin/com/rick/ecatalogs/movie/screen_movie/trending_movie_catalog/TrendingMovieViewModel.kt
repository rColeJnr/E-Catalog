package com.rick.ecatalogs.movie.screen_movie.trending_movie_catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rick.ecatalogs.movie.data_movie.data.repository.browse.ReleaseReminderRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.browse.TmdbBrowseRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie.CompositeTrendingMovieRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie.UserTrendingMovieDataRepository
import com.rick.ecatalogs.movie.data_movie.model.UserTrendingMovie
import com.rick.ecatalogs.movie.data_movie.model.tmdb.browse.TmdbGenre
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_movie.TrendingMovie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TrendingMovieViewModel @Inject constructor(
    compositeMovieRepository: CompositeTrendingMovieRepository,
    private val userDataRepository: UserTrendingMovieDataRepository,
    private val browseRepository: TmdbBrowseRepository,
    private val reminderRepository: ReleaseReminderRepository,
) : ViewModel() {

    /** null = trending; otherwise the TMDB genre whose most popular movies are listed. */
    private val _selectedGenre = MutableStateFlow<Int?>(null)
    val selectedGenre: StateFlow<Int?> = _selectedGenre.asStateFlow()

    private val trending = compositeMovieRepository
        .observeTrendingMovie(viewModelScope)
        .cachedIn(viewModelScope)

    private val movies: Flow<PagingData<UserTrendingMovie>> = _selectedGenre
        .flatMapLatest { genreId ->
            if (genreId == null) {
                trending
            } else {
                browseRepository.discoverMovies(genreId)
                    .cachedIn(viewModelScope)
                    .combine(userDataRepository.userData) { movies, userData ->
                        movies.map { UserTrendingMovie(it, userData) }
                    }
            }
        }
        .cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow<TrendingMovieUiState>(TrendingMovieUiState.Success(movies))
    val uiState: StateFlow<TrendingMovieUiState> = _uiState.asStateFlow()

    /** "Coming soon", with whether a release-day reminder is set for each movie. */
    val upcoming: StateFlow<List<Pair<TrendingMovie, Boolean>>> = combine(
        browseRepository.upcomingMovies(),
        reminderRepository.reminderIds,
    ) { movies, reminders -> movies.map { it to (it.id in reminders) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** "In theaters". */
    val nowPlaying: StateFlow<List<TrendingMovie>> = browseRepository.nowPlayingMovies()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val genres: StateFlow<List<TmdbGenre>> = flow { emit(browseRepository.movieGenres()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectGenre(genreId: Int?) {
        _selectedGenre.value = genreId
    }

    fun toggleReminder(movieId: Int) {
        val movie = upcoming.value.firstOrNull { it.first.id == movieId }?.first ?: return
        viewModelScope.launch { reminderRepository.toggleReminder(movie) }
    }

    fun onEvent(event: TrendingMovieUiEvent) {
        when (event) {
            is TrendingMovieUiEvent.UpdateTrendingMovieFavorite -> updateTrendingMovieFavorite(
                event.id,
                event.isFavorite
            )
        }
    }

    private fun updateTrendingMovieFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataRepository.setTrendingMovieFavoriteId(id, isFavorite)
        }
    }
}

sealed interface TrendingMovieUiState {
    data object Loading : TrendingMovieUiState
    data object Error : TrendingMovieUiState
    data class Success(val movies: Flow<PagingData<UserTrendingMovie>>) : TrendingMovieUiState
}

sealed interface TrendingMovieUiEvent {
    data class UpdateTrendingMovieFavorite(val id: Int, val isFavorite: Boolean) :
        TrendingMovieUiEvent
}
