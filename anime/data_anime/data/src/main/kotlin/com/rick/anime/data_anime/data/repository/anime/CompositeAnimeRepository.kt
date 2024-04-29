package com.rick.anime.data_anime.data.repository.anime

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rick.data.database_anime.model.asAnime
import com.rick.data.model_anime.UserAnime
import com.rick.data.model_anime.mapToUserAnime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * implements all anime repositories and provides all favorites
 * */
class CompositeAnimeRepository @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val userDataRepository: UserAnimeDataRepository
) : UserAnimeRepository {

    /**
     * Returns available news resources joined with user data
     */
    override fun observeAnime(scope: CoroutineScope): Flow<PagingData<UserAnime>> =
        animeRepository.getAnimes().cachedIn(scope)
            .combine(userDataRepository.animeUserData) { anime, userData ->
                anime.map { it.asAnime().mapToUserAnime(userData) }
            }


    override fun observeAnimeFavorite(): Flow<List<UserAnime>> =
        userDataRepository.animeUserData.map { it.animeFavoriteIds }.distinctUntilChanged()
            .flatMapLatest { favoriteIds ->
                when {
                    favoriteIds.isEmpty() -> flowOf(emptyList())
                    else -> animeRepository.getAnimeFavorites(favoriteIds)
                        .combine(userDataRepository.animeUserData) { anime, userData ->
                            anime.map { it.mapToUserAnime(userData) }
                        }
                }
            }

    override fun searchAnime(query: String): Flow<List<UserAnime>> =
        animeRepository.searchAnimes(query = query)
            .combine(userDataRepository.animeUserData) { anime, userData ->
                anime.map { it.mapToUserAnime(userData) }
            }

}