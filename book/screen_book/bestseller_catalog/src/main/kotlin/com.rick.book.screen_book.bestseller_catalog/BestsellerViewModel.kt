package com.rick.book.screen_book.bestseller_catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.data_book.repository.bestseller.CompositeBestsellerRepository
import com.rick.data.data_book.repository.bestseller.UserBestsellerDataRepository
import com.rick.data.model_book.bestseller.UserBestseller
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
    private val userDataRepository: UserBestsellerDataRepository
) : ViewModel() {

    lateinit var bestsellerUiState: StateFlow<BestsellerUIState>

    // private set
    private val nyKey: String
    var position: Int = 0
        private set

    init {

        System.loadLibrary(LIB_NAME)
        nyKey = getNYKey()

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

    private fun fetchBestsellers(bookGenre: Int = position) {
        position = bookGenre
        val genre = BookGenre.entries[position].listName

        bestsellerUiState = compositeBookRepository.observeBestseller(
            apiKey = nyKey, genre = genre, date = "current", viewModelScope
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

private external fun getNYKey(): String
private const val LIB_NAME = "book-keys"