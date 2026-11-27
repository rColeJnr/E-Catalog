package com.rick.ecatalogs.anime.data_anime.data.repository.manga

import androidx.paging.PagingData
import com.rick.ecatalogs.anime.data_anime.database.model.MangaEntity
import com.rick.ecatalogs.anime.data_anime.model.Manga
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [Manga]
 * */
interface MangaRepository {
    /**
     * Flow stream of manga (user manga)
     */
    fun getMangas(): Flow<PagingData<MangaEntity>>

    fun getMangaFavorites(favorites: Set<Int>): Flow<List<Manga>>

    fun searchManga(query: String): Flow<List<Manga>>

    /**
     * Background sync: downloads the first page of the top manga list and stores it when it changed
     * (the catalog then shows it without downloading). Returns the manga that are new since the
     * last sync, to notify about; nothing on the first sync, so a new install isn't notified about
     * the whole list. Throws on network errors (the sync retries).
     */
    suspend fun syncFirstPage(): List<Manga>
}
