package com.rick.anime.data_anime.data.repository.anime

import androidx.paging.PagingData
import com.rick.data.model_anime.UserAnime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [UserAnime]
 */
interface UserAnimeRepository {
    /**
     * Returns available animes as a stream.
     */
    fun observeAnime(scope: CoroutineScope): Flow<PagingData<UserAnime>>

    /**
     * observe the user's favorite anime
     * */
    fun observeAnimeFavorite(): Flow<List<UserAnime>>

    /**
     * Query the api for query
     */
    fun searchAnime(query: String): Flow<List<UserAnime>>
}