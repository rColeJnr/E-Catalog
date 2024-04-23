package com.rick.data.anime_favorite.repository.manga

import androidx.paging.PagingData
import com.rick.data.model_anime.UserManga
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface UserMangaRepository {
    /**
     * observe the user's manga
     * */
    fun observeManga(viewModelScope: CoroutineScope): Flow<PagingData<UserManga>>

    /**
     * observe the user's favorite manga
     * */
    fun observeMangaFavorite(): Flow<List<UserManga>>
}