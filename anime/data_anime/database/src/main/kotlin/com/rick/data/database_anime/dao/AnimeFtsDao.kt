package com.rick.data.database_anime.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.data.database_anime.model.AnimeFtsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeFtsDao {
    @Upsert
    suspend fun upsertAnimes(newsResources: List<AnimeFtsEntity>)

    @Query("SELECT animeId FROM anime_fts_table WHERE anime_fts_table MATCH :query")
    fun searchAllAnimes(query: String): Flow<List<String>>

    @Query("SELECT count(*) FROM anime_fts_table")
    fun getCount(): Flow<Int>
}
