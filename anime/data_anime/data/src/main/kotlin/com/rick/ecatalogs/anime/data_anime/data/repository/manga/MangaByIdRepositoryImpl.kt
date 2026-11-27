package com.rick.ecatalogs.anime.data_anime.data.repository.manga

import com.rick.ecatalogs.anime.data_anime.data.model.asEntity
import com.rick.ecatalogs.anime.data_anime.data.model.isListEntryOnly
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
import kotlinx.coroutines.flow.onStart
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
            val row = stored.first()
            emitAll(
                mangaDao.getMangaWithFilters(filterById = true, filterIds = setOf(id))
                    .filter { it.isNotEmpty() }
                    .map { it.first().asManga() }
                    .onStart {
                        if (row.isListEntryOnly) {
                            fetchFull(id)?.let { full ->
                                mangaDao.upsertManga(
                                    listOf(full.asEntity().copy(catalogPosition = row.catalogPosition))
                                )
                            }
                        }
                    }
            )
        } else {
            fetchFull(id)?.let { emit(it.asEntity().asManga()) }
        }
    }

    private suspend fun fetchFull(id: Int) = try {
        network.fetchMangaFull(id).data
    } catch (e: Exception) {
        null
    }
}
