package com.rick.ecatalogs.anime.data_anime.data.repository.anime

import com.rick.ecatalogs.anime.data_anime.model.Anime
import kotlinx.coroutines.flow.Flow

interface AnimeByIdRepository {
    fun getAnimeById(id: Int): Flow<Anime>
}