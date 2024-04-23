package com.rick.screen_anime.search_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.anime_favorite.repository.CompositeAnimeRepository
import com.rick.data.anime_favorite.repository.UserAnimeDataRepository
import com.rick.data.anime_favorite.repository.UserMangaDataRepository
import com.rick.data.model_anime.UserAnime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchAnimeViewModel @Inject constructor(
    private val userAnimeDataRepository: UserAnimeDataRepository,
    private val userMangaDataRepository: UserMangaDataRepository,
    private val compositeAnimeRepository: CompositeAnimeRepository
) : ViewModel() {

    private val _searchList: MutableLiveData<List<UserAnime>> by lazy { MutableLiveData<List<UserAnime>>() }
    val searchList: LiveData<List<UserAnime>> get() = _searchList

    private val _searchLoading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
    val searchLoading: LiveData<Boolean> get() = _searchLoading

    private val _searchError: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val searchError: LiveData<String> get() = _searchError

    val searchUiAction: (SearchUiAction) -> Unit
    val searchUiState: StateFlow<SearchUiState>

    init {

        val actionStateFlow = MutableSharedFlow<SearchUiAction>()
        val searchJikan =
            actionStateFlow.filterIsInstance<SearchUiAction.SearchJikan>().distinctUntilChanged()
        val searchAnime =
            actionStateFlow.filterIsInstance<SearchUiAction.SearchAnime>().distinctUntilChanged()
        val searchManga =
            actionStateFlow.filterIsInstance<SearchUiAction.SearchManga>().distinctUntilChanged()

        viewModelScope.launch {
            searchJikan.collectLatest {
                searchAnime(it.query)
                searchManga(it.query)
            }
        }

        viewModelScope.launch {
            searchManga.collectLatest {
                //TODO later feature
            }
        }

        searchUiState = combine(
            searchJikan, searchAnime, searchManga, ::Triple
        ).map { (jikan, anime, manga) ->
            SearchUiState(
                jikanQuery = jikan.query, animeQuery = anime.query, mangaQuery = manga.query
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000L),
            initialValue = SearchUiState()
        )

        searchUiAction = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    private fun searchAnime(title: String) {
        viewModelScope.launch {
//            compositeAnimeRepository.searchAnime(query = title).collectLatest { result ->
//                when (result) {
//                    is Resource.Error -> {
//                        _searchError.postValue(result.message)
//                    }
//
//                    is Resource.Loading -> {
//                        _searchLoading.postValue(result.isLoading)
//                    }
//
//                    is Resource.Success -> {
//                        _searchList.postValue(result.data!!)
//                    }
//                }
//            }
        }
    }

    private fun searchManga(title: String) {
        viewModelScope.launch {
//            compositeAnimeRepository.searchManga(query = title).collectLatest { result ->
//                when (result) {
//                    is Resource.Error -> {
//                        _searchError.postValue(result.message ?: "Error")
//                    }
//
//                    is Resource.Loading -> {
//                        _searchLoading.postValue(result.isLoading)
//                    }
//
//                    is Resource.Success -> { //TODO, improve code performance, also, this doesnt work
//                        var jikan: Jikan? = null
//                        val animelist = result.data!!
//                        val favoritelist: MutableList<UserJikan> = mutableListOf()
//                        compositeAnimeRepository.observeMangaFavorite().map {
//                            favoritelist.addAll(it)
//                        }
//                        for (favorite in favoritelist) {
//                            for (anime in animelist) {
//                                if (favorite.id == anime.malId) {
//
//                                }
//                            }
//                        }
////                        _searchList.postValue()
//                    }
//                }
//            }
        }
    }

    fun onEvent(event: SearchUiAction) {
        when (event) {
            is SearchUiAction.UpdateFavorite -> updateFavorite(
                event.id,
                event.isFavorite
            )

            else -> {}
        }
    }

    private fun updateFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
//            if (type == "Manga") {
//                userDataRepository.setMangaFavoriteId(id, isFavorite)
//            } else {
//                userDataRepository.setAnimeFavoriteId(id, isFavorite)
//            }
        }
    }
}

sealed class SearchUiAction {
    data class SearchAnime(val query: String) : SearchUiAction()
    data class SearchManga(val query: String) : SearchUiAction()
    data class SearchJikan(val query: String) : SearchUiAction()
    data class UpdateFavorite(val id: Int, val isFavorite: Boolean) :
        SearchUiAction()
}

data class SearchUiState(
    val jikanQuery: String? = null, val animeQuery: String? = null, val mangaQuery: String? = null
)