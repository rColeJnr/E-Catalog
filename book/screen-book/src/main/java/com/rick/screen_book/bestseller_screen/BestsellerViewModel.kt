package com.rick.screen_book.bestseller_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.data_book.BookRepository
import com.rick.data_book.favorite.Favorite
import com.rick.data_book.nytimes.model.NYBook
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BestsellerViewModel @Inject constructor(
    private val repository: BookRepository
): ViewModel() {

    val pagingDataFlow: Flow<PagingData<NYBook>>
    private val nyKey: String
    private var position: Int = 0
    private var favorite: Favorite? = null

    init {

        System.loadLibrary(LIB_NAME)
        nyKey = getNYKey()

        pagingDataFlow = fetchBestsellers().cachedIn(viewModelScope)

    }

    fun onEvent(event: BestsellerEvents) {
        when (event) {
            is BestsellerEvents.SelectedGenre -> fetchBestsellers(event.bookGenre)
            is BestsellerEvents.OnFavoriteClick -> { onFavoriteClick(event.book) }
            is BestsellerEvents.OnRemoveFavorite -> { onRemoveFavorite() }
        }
    }

    //  TODO add bestseller genre selector
    private fun fetchBestsellers(bookGenre: Int = position): Flow<PagingData<NYBook>>{
        position = bookGenre
        return repository.getBestsellers(apiKey = nyKey, bookGenre = BookGenre.values()[position].listName)
    }

    private fun onFavoriteClick(book: NYBook) {
        favorite = Favorite(
            title = book.title,
            author = book.author,
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(favorite!!)
        }
    }

    private fun onRemoveFavorite() {
        favorite?.let { favorite ->
            viewModelScope.launch(Dispatchers.IO) {
                repository.delete(favorite)
            }
        }
        favorite = null
    }
}

private external fun getNYKey(): String
private const val LIB_NAME = "book-keys"