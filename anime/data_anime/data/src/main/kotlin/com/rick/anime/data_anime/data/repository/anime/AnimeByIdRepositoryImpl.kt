package com.rick.anime.data_anime.data.repository.anime

import com.rick.data.database_anime.dao.AnimeDao
import com.rick.data.database_anime.model.asAnime
import com.rick.data.model_anime.Anime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AnimeByIdRepositoryImpl @Inject constructor(
    private val animeDao: AnimeDao
) : AnimeByIdRepository {

    override fun getAnimeById(id: Int): Flow<Anime> =
        animeDao.getAnimeWithFilters(filterById = true, filterIds = setOf(id))
            .filter { it.isNotEmpty() }
            .map { it.first().asAnime() }


}
