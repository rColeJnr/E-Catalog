package com.rick.ecatalogs.anime.data_anime.data.repository.anime

import com.rick.ecatalogs.anime.data_anime.data.model.asEntity
import com.rick.ecatalogs.anime.data_anime.data.model.isListEntryOnly
import com.rick.ecatalogs.anime.data_anime.network.JikanNetworkDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import com.rick.ecatalogs.anime.data_anime.database.dao.AnimeDao
import com.rick.ecatalogs.anime.data_anime.database.model.asAnime
import com.rick.ecatalogs.anime.data_anime.model.Anime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class AnimeByIdRepositoryImpl @Inject constructor(
    private val animeDao: AnimeDao,
    private val network: JikanNetworkDataSource,
) : AnimeByIdRepository {

    /**
     * From the stored top list when it's there; otherwise (opened from a season row, a genre list
     * or a recommendation) from Jikan, without storing it.
     */
    override fun getAnimeById(id: Int): Flow<Anime> = flow {
        val stored = animeDao.getAnimeWithFilters(filterById = true, filterIds = setOf(id)).first()
        if (stored.isNotEmpty()) {
            val row = stored.first()
            emitAll(
                animeDao.getAnimeWithFilters(filterById = true, filterIds = setOf(id))
                    .filter { it.isNotEmpty() }
                    .map { it.first().asAnime() }
                    .onStart {
                        if (row.isListEntryOnly) {
                            fetchFull(id)?.let { full ->
                                animeDao.upsertAnime(
                                    listOf(full.asEntity().copy(catalogPosition = row.catalogPosition))
                                )
                            }
                        }
                    }
            )
        } else {
            fetchFull(id)?.let { emit(it.asEntity().asAnime()) }
        }
    }

    private suspend fun fetchFull(id: Int) = try {
        network.fetchAnimeFull(id).data
    } catch (e: Exception) {
        null
    }
}
