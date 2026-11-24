package com.rick.ecatalogs.book.screen_book.bestseller_catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.ecatalogs.book.data_book.data.repository.bestseller.BookReviewRepository
import com.rick.ecatalogs.book.data_book.data.repository.bestseller.UserBestsellerDataRepository
import com.rick.ecatalogs.book.data_book.data.repository.bestseller.UserBestsellerRepository
import com.rick.ecatalogs.book.data_book.model.bestseller.BookReview
import com.rick.ecatalogs.book.data_book.model.bestseller.UserBestseller
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BestsellerDetailsViewModel @Inject constructor(
    bestsellerRepository: UserBestsellerRepository,
    private val reviewRepository: BookReviewRepository,
    private val userDataRepository: UserBestsellerDataRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /** Navigation argument: the book's ISBN-13 (deep link `bestseller_details/{isbn}`). */
    private val isbn: String = checkNotNull(savedStateHandle[ISBN_ARG])

    val bookState: StateFlow<BookUiState> = bestsellerRepository.observeBestsellerById(isbn)
        .map { book -> if (book == null) BookUiState.Unavailable else BookUiState.Success(book) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BookUiState.Loading)

    private val _reviewState = MutableStateFlow<ReviewUiState>(ReviewUiState.Loading)
    val reviewState: StateFlow<ReviewUiState> = _reviewState.asStateFlow()

    init {
        loadReview()
    }

    fun loadReview() {
        _reviewState.value = ReviewUiState.Loading
        viewModelScope.launch {
            _reviewState.value = try {
                reviewRepository.getReview(isbn)
                    ?.let(ReviewUiState::Found)
                    ?: ReviewUiState.NotReviewed
            } catch (e: Exception) {
                ReviewUiState.Error
            }
        }
    }

    fun toggleFavorite() {
        val book = (bookState.value as? BookUiState.Success)?.book ?: return
        viewModelScope.launch {
            userDataRepository.setBestsellerFavoriteId(book.id, !book.isFavorite)
        }
    }
}

sealed interface BookUiState {
    data object Loading : BookUiState
    data class Success(val book: UserBestseller) : BookUiState

    /** The book is no longer stored (e.g. it dropped off the list since the screen was linked). */
    data object Unavailable : BookUiState
}

sealed interface ReviewUiState {
    data object Loading : ReviewUiState
    data class Found(val review: BookReview) : ReviewUiState
    data object NotReviewed : ReviewUiState
    data object Error : ReviewUiState
}

private const val ISBN_ARG = "isbn"
