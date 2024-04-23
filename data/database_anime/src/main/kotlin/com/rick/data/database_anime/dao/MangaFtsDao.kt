package com.rick.data.database_anime.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.data.database_anime.model.MangaFtsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaFtsDao {
    @Upsert
    suspend fun upsertMangas(mangas: List<MangaFtsEntity>)

    @Query("SELECT mangaId FROM manga_fts_table WHERE mangaId MATCH :query")
    fun searchAllMangas(query: String): Flow<List<String>>

    @Query("SELECT count(*) FROM manga_fts_table")
    fun getCount(): Flow<Int>
}
