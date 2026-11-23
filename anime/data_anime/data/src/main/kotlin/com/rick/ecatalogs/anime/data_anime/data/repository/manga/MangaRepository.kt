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

}