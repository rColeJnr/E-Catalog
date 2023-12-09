package com.rick.screen_book.favorite_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.core.Resource
import com.rick.data_book.BookRepository
import com.rick.data_book.favorite.Favorite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookFavoriteViewModel @Inject constructor(private val repo: BookRepository) : ViewModel() {

    private val _books = MutableLiveData<List<Favorite>>()
    val books : LiveData<List<Favorite>> get() = _books

    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> get() = _loading

    init {
        getFavorites()
    }

    private fun getFavorites() {
        viewModelScope.launch {
            repo.getFavoriteBook().collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _loading.postValue(resource.isLoading)
                    }
                    is Resource.Success -> {
                        _books.postValue(resource.data ?: listOf())
                    }
                    else -> {}
                }
            }
        }
    }

    fun onEvent(event: FavoriteEvents) {
        when (event) {
            is FavoriteEvents.InsertFavorite -> insertFavorite(event.fav)
            is FavoriteEvents.DeleteFavorite -> deleteFavorite(event.fav)
        }
    }

    private fun insertFavorite(fav: Favorite) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insert(fav)
        }
    }

    private fun deleteFavorite(fav: Favorite) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.delete(fav)
        }
    }

}

sealed class FavoriteEvents {
    data class InsertFavorite(val fav: Favorite): FavoriteEvents()
    data class DeleteFavorite(val fav: Favorite): FavoriteEvents()
}