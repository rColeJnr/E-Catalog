package com.rick.data.database_anime.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.data.database_anime.model.AnimeRecentSearchQueryEntity
import com.rick.data.database_anime.model.MangaRecentSearchQueryEntity
import kotlinx.coroutines.flow.Flow


/**
 * Dao for [AnimeRecentSearchQueryEntity] access
 * */
@Dao
interface MangaRecentSearchQueryDao {

    @Query("SELECT * FROM manga_recent_search_queries_table ORDER BY queriedDate DESC LIMIT :limit")
    fun getMangaRecentSearchQueryEntities(limit: Int): Flow<List<MangaRecentSearchQueryEntity>>

    @Upsert
    suspend fun insertOrReplaceMangaRecentSearchQuery(recentSearchQuery: MangaRecentSearchQueryEntity)

    @Query(value = "DELETE FROM manga_recent_search_queries_table")
    suspend fun clearMangaRecentSearchQueries()

}