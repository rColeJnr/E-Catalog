package com.rick.ecatalogs.anime.screen_anime.anime_catalog

import com.rick.ecatalogs.anime.data_anime.model.browse.JikanItem
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.rick.ecatalogs.anime.data_anime.model.browse.JikanGenre
import com.rick.ecatalogs.anime.data_anime.model.browse.AnimeList
import com.rick.ecatalogs.anime.data_anime.data.repository.browse.JikanBrowseRepository
import androidx.paging.map
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.ecatalogs.anime.screen_anime.common.JikanUiEvents
import com.rick.ecatalogs.anime.data_anime.data.repository.anime.CompositeAnimeRepository
import com.rick.ecatalogs.anime.data_anime.data.repository.anime.UserAnimeDataRepository
import com.rick.ecatalogs.anime.data_anime.model.UserAnime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnimeCatalogViewModel @Inject constructor(
    private val userDataRepository: UserAnimeDataRepository,
    private val compositeAnimeRepository: CompositeAnimeRepository,
    private val browseRepository: JikanBrowseRepository,
) : ViewModel() {


    /** Which list the catalog shows; [AnimeList.POPULAR] with no genre is the stored list. */
    private val _list = MutableStateFlow(AnimeList.POPULAR)
    val selectedList: StateFlow<AnimeList> = _list.asStateFlow()

    /** A Jikan genre id, or null for all genres. */
    private val _genre = MutableStateFlow<Int?>(null)
    val selectedGenre: StateFlow<Int?> = _genre.asStateFlow()

    private val stored = compositeAnimeRepository.observeAnime(viewModelScope).cachedIn(viewModelScope)

    val pagingDataFlow: Flow<PagingData<UserAnime>> = combine(_list, _genre) { list, genre -> list to genre }
        .flatMapLatest { (list, genre) ->
            if (list == AnimeList.POPULAR && genre == null) {
                stored
            } else {
                browseRepository.browseAnime(list, genre)
                    .cachedIn(viewModelScope)
                    .combine(userDataRepository.animeUserData) { items, userData ->
                        items.map { UserAnime(it, userData) }
                    }
            }
        }
        .cachedIn(viewModelScope)

    val genres: StateFlow<List<JikanGenre>> = flow { emit(browseRepository.animeGenres()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val seasonNow: StateFlow<List<JikanItem>> = browseRepository.seasonNow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())


    fun selectList(list: AnimeList) {
        _list.value = list
    }

    /** "Surprise me": a random (safe for work) anime id, or null if none could be picked. */
    suspend fun randomAnimeId(): Int? = runCatching { browseRepository.randomAnimeId() }.getOrNull()

    fun selectGenre(genreId: Int?) {
        _genre.value = genreId
    }

    fun onEvent(event: JikanUiEvents) {
        when (event) {
            is JikanUiEvents.UpdateAnimeFavorite -> {
                updateAnimeFavorite(id = event.id, isFavorite = event.isFavorite)
            }

            is JikanUiEvents.UpdateMangaFavorite -> {
                // Do nothing
            }
        }
    }

    private fun updateAnimeFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataRepository.setAnimeFavoriteId(id, isFavorite)
        }
    }

    private fun fetchAnime(): Flow<PagingData<UserAnime>> =
        compositeAnimeRepository.observeAnime(viewModelScope)


}

private const val LOCATION_QUERY = "LocationQuery"
private const val FOLDER_ID = "b1g3qbtiprdplbrpod5i"