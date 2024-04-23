package com.rick.data.anime_favorite.repository.manga

import androidx.paging.PagingData
import com.rick.data.database_anime.model.MangaEntity
import com.rick.data.model_anime.Manga
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
}