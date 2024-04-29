package com.rick.anime.data_anime.data.repository.manga

import com.rick.data.model_anime.Manga
import kotlinx.coroutines.flow.Flow

interface MangaByIdRepository {
    fun getMangaById(id: Int): Flow<Manga>
}