package com.rick.ecatalogs.anime.data_anime.data.repository.manga

import com.rick.ecatalogs.anime.data_anime.model.Manga
import kotlinx.coroutines.flow.Flow

interface MangaByIdRepository {
    fun getMangaById(id: Int): Flow<Manga>
}