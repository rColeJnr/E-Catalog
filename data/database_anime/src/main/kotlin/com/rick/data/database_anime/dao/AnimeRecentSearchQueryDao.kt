package com.rick.data.database_anime.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.data.database_anime.model.AnimeRecentSearchQueryEntity
import kotlinx.coroutines.flow.Flow


/**
 * Dao for [AnimeRecentSearchQueryEntity] access
 * */
@Dao
interface AnimeRecentSearchQueryDao {

    @Query("SELECT * FROM anime_recent_search_queries_table ORDER BY queriedDate DESC LIMIT :limit")
    fun getAnimeRecentSearchQueryEntities(limit: Int): Flow<List<AnimeRecentSearchQueryEntity>>

    @Upsert
    suspend fun insertOrReplaceAnimeRecentSearchQuery(recentSearchQuery: AnimeRecentSearchQueryEntity)

    @Query(value = "DELETE FROM anime_recent_search_queries_table")
    suspend fun clearAnimeRecentSearchQueries()

}