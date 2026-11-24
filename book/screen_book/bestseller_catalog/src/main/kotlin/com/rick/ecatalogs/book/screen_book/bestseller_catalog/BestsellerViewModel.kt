package com.rick.ecatalogs.book.screen_book.bestseller_catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.ecatalogs.book.data_book.data.repository.bestseller.CompositeBestsellerRepository
import com.rick.ecatalogs.book.data_book.data.repository.bestseller.UserBestsellerDataRepository
import com.rick.ecatalogs.book.data_book.model.bestseller.UserBestseller
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BestsellerViewModel @Inject constructor(
    private val compositeBookRepository: CompositeBestsellerRepository,
    private val userDataRepository: UserBestsellerDataRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /** Index into [BookGenre.entries] of the list being shown. */
    private val selectedGenre = MutableStateFlow(
        // A notification deep link can open a specific genre (nav argument "genre" = list name)
        (BookGenre.fromListName(savedStateHandle.get<String>(GENRE_ARG)) ?: BookGenre.DEFAULT)
            .ordinal
    )

    val selectedGenrePosition: Int get() = selectedGenre.value

    /**
     * The selected genre's stored list, for the "Top picks" carousel. It's filled by
     * [bestsellerPagingData] (no network here), so it's Loading until the first page is stored.
     */
    val bestsellerUiState: StateFlow<BestsellerUIState> = selectedGenre
        .flatMapLatest { position ->
            compositeBookRepository.observeBestseller(
                genre = BookGenre.entries[position].listName,
                date = "current",
                viewModelScope = viewModelScope,
            )
        }
        .map { books ->
            if (books.isEmpty()) BestsellerUIState.Loading else BestsellerUIState.Success(books)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BestsellerUIState.Loading,
        )

    /** The selected genre's list, paged (NYT pages lists 20 books at a time). */
    val bestsellerPagingData: Flow<PagingData<UserBestseller>> = selectedGenre
        .flatMapLatest { position ->
            compositeBookRepository.observeBestsellerPaging(
                genre = BookGenre.entries[position].listName,
                scope = viewModelScope,
            )
        }
        .cachedIn(viewModelScope)

    fun onEvent(event: BestsellerEvents) {
        when (event) {
            is BestsellerEvents.SelectedGenre -> selectedGenre.value = event.bookGenre
            is BestsellerEvents.UpdateBestsellerFavorite ->
                updateBestsellerFavorite(event.id, event.isFavorite)
        }
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
