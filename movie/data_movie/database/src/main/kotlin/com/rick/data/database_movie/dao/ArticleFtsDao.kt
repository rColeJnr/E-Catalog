package com.rick.data.database_movie.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.data.database_movie.model.ArticleFtsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleFtsDao {
    @Upsert
    suspend fun upsertArticles(articles: List<ArticleFtsEntity>)

    @Query("SELECT articleId FROM article_fts_table WHERE article_fts_table MATCH :query")
    fun searchAllArticles(query: String): Flow<List<String>>

    @Query("SELECT count(*) FROM article_fts_table")
    fun getCount(): Flow<Int>
}
