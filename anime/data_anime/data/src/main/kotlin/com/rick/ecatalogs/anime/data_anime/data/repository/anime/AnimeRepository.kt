package com.rick.ecatalogs.anime.data_anime.data.repository.anime

import androidx.paging.PagingData
import com.rick.ecatalogs.anime.data_anime.database.model.AnimeEntity
import com.rick.ecatalogs.anime.data_anime.model.Anime
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [Anime]
 * */
interface AnimeRepository {
    fun getAnimes(): Flow<PagingData<AnimeEntity>>

    fun getAnimeFavorites(favorites: Set<Int>): Flow<List<Anime>>

    fun searchAnimes(query: String): Flow<List<Anime>>

}