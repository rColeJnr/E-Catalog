package com.rick.screen_anime.compose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.core.Resource
import com.rick.data_anime.JikanRepository
import com.rick.data_anime.model_jikan.Jikan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JikanFavoriteViewModel @Inject constructor(private val repo: JikanRepository) : ViewModel() {

    private val _anime = MutableLiveData<List<Jikan>>()
    val anime: LiveData<List<Jikan>> get() = _anime

    private val _loadingAnime = MutableLiveData<Boolean>()
    val loadingAnime: LiveData<Boolean> get() = _loadingAnime

    private val _manga = MutableLiveData<List<Jikan>>()
    val manga: LiveData<List<Jikan>> get() = _manga

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
}