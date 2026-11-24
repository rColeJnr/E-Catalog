package com.rick.ecatalogs.anime.data_anime.data.repository.manga

import com.rick.ecatalogs.anime.data_anime.data.model.asEntity
import com.rick.ecatalogs.anime.data_anime.network.JikanNetworkDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import com.rick.ecatalogs.anime.data_anime.database.dao.MangaDao
import com.rick.ecatalogs.anime.data_anime.database.model.asManga
import com.rick.ecatalogs.anime.data_anime.model.Manga
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MangaByIdRepositoryImpl @Inject constructor(
    private val mangaDao: MangaDao,
    private val network: JikanNetworkDataSource,
) : MangaByIdRepository {

    /**
     * From the stored top list when it's there; otherwise (opened from a season row, a genre list
     * or a recommendation) from Jikan, without storing it.
     */
    override fun getMangaById(id: Int): Flow<Manga> = flow {
        val stored = mangaDao.getMangaWithFilters(filterById = true, filterIds = setOf(id)).first()
        if (stored.isNotEmpty()) {
            emitAll(
                mangaDao.getMangaWithFilters(filterById = true, filterIds = setOf(id))
                    .filter { it.isNotEmpty() }
                    .map { it.first().asManga() }
            )
        } else {
            val fetched = try {
                network.fetchMangaFull(id).data
            } catch (e: Exception) {
                null
            }
            fetched?.let { emit(it.asEntity().asManga()) }
        }
    }


}
