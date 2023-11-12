package com.rick.screen_book.bestseller_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.data_book.BookRepository
import com.rick.data_book.nytimes.model.NYBook
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


private const val LIB_NAME = "book-keys"
@HiltViewModel
class BestsellerViewModel @Inject constructor(
    private val repository: BookRepository
): ViewModel() {

    val pagingDataFlow: Flow<PagingData<NYBook>>
    private val nyKey: String
    init {

        System.loadLibrary(LIB_NAME)
        nyKey = getNYKey()

        pagingDataFlow = fetchBestsellers(nyKey).cachedIn(viewModelScope)

    }

    private fun fetchBestsellers(bookGenre: String = BookGenre.DEFAULT.listName) =
        repository.getBestsellers(apiKey = nyKey, bookGenre = bookGenre)

    fun onEvent(event: BestsellerEvents) {
        when (event) {
            is BestsellerEvents.SelectedGenre -> fetchBestsellers(event.bookGenre.listName)
        }
    }

}

private external fun getNYKey(): String