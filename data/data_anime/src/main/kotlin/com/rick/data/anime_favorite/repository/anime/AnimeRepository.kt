package com.rick.data.anime_favorite.repository.anime

import androidx.paging.PagingData
import com.rick.data.database_anime.model.AnimeEntity
import com.rick.data.model_anime.Anime
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [Anime]
 * */
interface AnimeRepository {
    fun getAnimes(): Flow<PagingData<AnimeEntity>>

    fun getAnimeFavorites(favorites: Set<Int>): Flow<List<Anime>>

//    fun searchAnimes(): Flow<PagingData<Anime>>
}