package com.rick.screen_anime.favorite_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.core.Resource
import com.rick.data_anime.JikanRepository
import com.rick.data_anime.favorite.JikanFavorite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JikanFavoriteViewModel @Inject constructor(private val repo: JikanRepository) : ViewModel() {

    private val _anime = MutableLiveData<List<JikanFavorite>>()
    val anime: LiveData<List<JikanFavorite>> get() = _anime

    private val _loadingAnime = MutableLiveData<Boolean>()
    val loadingAnime: LiveData<Boolean> get() = _loadingAnime

    private val _manga = MutableLiveData<List<JikanFavorite>>()
    val manga: LiveData<List<JikanFavorite>> get() = _manga

    private val _loadingManga = MutableLiveData<Boolean>()
    val loadingManga: LiveData<Boolean> get() = _loadingManga

    init {
        getFavorites()
    }

    private fun getFavorites() {
        viewModelScope.launch {
            repo.getFavoriteAnime().collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _loadingAnime.postValue(resource.isLoading)
                    }
                    is Resource.Success -> {
                        _anime.postValue(resource.data ?: listOf())
                    }
                    else -> {}
                }
            }
            repo.getFavoriteManga().collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _loadingManga.postValue(resource.isLoading)
                    }
                    is Resource.Success -> {
                        _manga.postValue(resource.data ?: listOf())
                    }
                    else -> {}
                }
            }
        }
    }

    fun onEvent(event: JikanEvents) {
        when (event) {
            is JikanEvents.InsertFavorite -> insertFavorite(event.fav)
            is JikanEvents.DeleteFavorite -> deleteFavorite(event.fav)
        }
    }

    private fun insertFavorite(fav: JikanFavorite) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertFavorite(fav)
        }
    }

    private fun deleteFavorite(fav: JikanFavorite) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFavorite(fav)
        }
    }

}

sealed class JikanEvents {
    data class InsertFavorite(val fav: JikanFavorite): JikanEvents()
    data class DeleteFavorite(val fav: JikanFavorite): JikanEvents()
}

