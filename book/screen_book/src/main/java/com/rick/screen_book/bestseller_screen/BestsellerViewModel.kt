package com.rick.screen_book.bestseller_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.rick.data.model_book.Favorite
import com.rick.data_book.BookRepository
import com.rick.data_book.nytimes.model.NYBook
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BestsellerViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    lateinit var pagingDataFlow: Flow<PagingData<NYBook>>

    // private set
    private val nyKey: String
    var position: Int = 0
        private set
    private var favorite: com.rick.data.model_book.Favorite? = null

    init {

        System.loadLibrary(LIB_NAME)
        nyKey = getNYKey()

    }

    fun onEvent(event: BestsellerEvents) {
        when (event) {
            is BestsellerEvents.SelectedGenre -> fetchBestsellers(event.bookGenre)
            is BestsellerEvents.OnFavoriteClick -> {
                onFavoriteClick(event.book)
            }

            is BestsellerEvents.OnRemoveFavorite -> {
                onRemoveFavorite()
            }
        }
    }

    //  TODO add bestseller genre selector
    private fun fetchBestsellers(bookGenre: Int = position) {
        position = bookGenre
        val genre = BookGenre.values()[position].listName

        pagingDataFlow = repository.getBestsellers(
            apiKey = nyKey, bookGenre = genre
        )
    }

    private fun onFavoriteClick(book: NYBook) {
        favorite = com.rick.data.model_book.Favorite(
            title = book.title,
            author = book.author,
            image = book.bookImage,
            isFavorite = false // TODO
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