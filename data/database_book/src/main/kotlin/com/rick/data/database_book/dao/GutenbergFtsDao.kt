package com.rick.data.database_book.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.data.database_book.model.GutenbergFtsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GutenbergFtsDao {
    @Upsert
    suspend fun upsertGutenbergs(gutenbergs: List<GutenbergFtsEntity>)

    @Query("SELECT gutenbergId FROM gutenberg_fts_table WHERE gutenberg_fts_table MATCH :query")
    fun searchAllGutenbergs(query: String): Flow<List<String>>

    @Query("SELECT count(*) FROM gutenberg_fts_table")
    fun getCount(): Flow<Int>
}
