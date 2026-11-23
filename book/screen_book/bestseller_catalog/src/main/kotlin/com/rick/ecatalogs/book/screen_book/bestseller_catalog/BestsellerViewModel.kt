package com.rick.ecatalogs.book.screen_book.bestseller_catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.ecatalogs.book.data_book.data.repository.bestseller.CompositeBestsellerRepository
import com.rick.ecatalogs.book.data_book.data.repository.bestseller.UserBestsellerDataRepository
import com.rick.ecatalogs.book.data_book.model.bestseller.UserBestseller
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BestsellerViewModel @Inject constructor(
    private val compositeBookRepository: CompositeBestsellerRepository,
    private val userDataRepository: UserBestsellerDataRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    lateinit var bestsellerUiState: StateFlow<BestsellerUIState>

    // private set
    private var position: Int = 0

    /** Index into [BookGenre.entries] of the list being shown. */
    val selectedGenrePosition: Int get() = position

    init {
        // A notification deep link can open a specific genre (nav argument "genre" = NYT list name)
        val initialGenre = BookGenre.fromListName(savedStateHandle.get<String>(GENRE_ARG))
            ?: BookGenre.DEFAULT
        onEvent(BestsellerEvents.SelectedGenre(initialGenre.ordinal))

    }

    fun onEvent(event: BestsellerEvents) {
        when (event) {
            is BestsellerEvents.SelectedGenre -> {
                fetchBestsellers(event.bookGenre)
            }

            is BestsellerEvents.UpdateBestsellerFavorite -> {
                updateBestsellerFavorite(event.id, event.isFavorite)
            }
        }
    }

    private fun fetchBestsellers() {

    }


    private fun fetchBestsellers(bookGenre: Int = position) {
        position = bookGenre
        val genre = BookGenre.entries[position].listName

        bestsellerUiState = compositeBookRepository.observeBestseller(
            genre = genre, date = "current", viewModelScope
        ).map<List<UserBestseller>, BestsellerUIState>(BestsellerUIState::Success)
            .catch { BestsellerUIState.Error }
            .onStart { emit(BestsellerUIState.Loading) }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(1000),
                initialValue = BestsellerUIState.Loading
            )
    }

    private fun updateBestsellerFavorite(id: String, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataRepository.setBestsellerFavoriteId(id, isFavorite)
        }
    }
}

sealed interface BestsellerUIState {
    data object Loading : BestsellerUIState
    data object Error : BestsellerUIState
    data class Success(val bestsellers: List<UserBestseller>) : BestsellerUIState
}

sealed interface BestsellerCarouselUIState {
    data object Loading : BestsellerCarouselUIState
    data object Error : BestsellerCarouselUIState
    data class Success(val bestsellers: List<UserBestseller>) : BestsellerCarouselUIState
}

private const val GENRE_ARG = "genre"
