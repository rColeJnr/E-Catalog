package com.rick.anime.data_anime.data.repository.anime

import com.rick.data.model_anime.Anime
import kotlinx.coroutines.flow.Flow

interface AnimeByIdRepository {
    fun getAnimeById(id: Int): Flow<Anime>
}