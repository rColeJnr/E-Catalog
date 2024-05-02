package com.rick.anime.data_anime.data.repository.manga

import com.rick.data.database_anime.dao.MangaDao
import com.rick.data.database_anime.model.asManga
import com.rick.data.model_anime.Manga
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MangaByIdRepositoryImpl @Inject constructor(
    private val mangaDao: MangaDao
) : MangaByIdRepository {

    override fun getMangaById(id: Int): Flow<Manga> =
        mangaDao.getMangaWithFilters(filterById = true, filterIds = setOf(id))
            .filter { it.isNotEmpty() }
            .map { it.first().asManga() }


}
