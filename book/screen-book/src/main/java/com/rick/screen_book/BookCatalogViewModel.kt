package com.rick.screen_book

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.data_book.BookRepository
import com.rick.data_book.favorite.Favorite
import com.rick.data_book.gutenberg.model.Book
import com.rick.screen_book.favorite_screen.FavoriteEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookCatalogViewModel @Inject constructor(
    private val repository: BookRepository
): ViewModel() {

    val pagingDataFlow: Flow<PagingData<Book>>

    init {
        pagingDataFlow = fetchBooks().cachedIn(viewModelScope)
    }

    private fun fetchBooks(): Flow<PagingData<Book>> =
        repository.getBooks()

    fun onEvent(event: FavoriteEvents) {
        when (event) {
            is FavoriteEvents.InsertFavorite -> insertFavorite(event.fav)
            else -> {}
        }
    }

    private fun insertFavorite(fav: Favorite) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(fav)
            Log.d("TAGF", "am inserting this fav book")
        }
    }

}