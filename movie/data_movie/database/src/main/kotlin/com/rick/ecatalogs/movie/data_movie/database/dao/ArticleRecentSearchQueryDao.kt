package com.rick.ecatalogs.movie.data_movie.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.ecatalogs.movie.data_movie.database.model.ArticleRecentSearchQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleRecentSearchQueryDao {

    @Query("SELECT * FROM article_recent_search_queries_table ORDER BY queriedDate DESC LIMIT :limit")
    fun getArticleRecentSearchQueryEntities(limit: Int): Flow<List<ArticleRecentSearchQueryEntity>>

    @Upsert
    suspend fun insertOrReplaceArticleRecentSearchQuery(recentSearchQuery: ArticleRecentSearchQueryEntity)

    @Query(value = "DELETE FROM article_recent_search_queries_table")
    suspend fun clearArticleRecentSearchQueries()

}