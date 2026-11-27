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

    /**
     * Background sync: downloads the first page of the top anime list and stores it when it changed
     * (the catalog then shows it without downloading). Returns the anime that are new since the
     * last sync, to notify about; nothing on the first sync, so a new install isn't notified about
     * the whole list. Throws on network errors (the sync retries).
     */
    suspend fun syncFirstPage(): List<Anime>
}
