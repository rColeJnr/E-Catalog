package com.rick.movie.data_movie.data.repository.article

import androidx.paging.PagingData
import com.rick.data.model_movie.UserArticle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface UserArticlesRepository {

    fun observeArticle(
        apiKey: String,
        viewModelScope: CoroutineScope
    ): Flow<PagingData<UserArticle>>

    /**
     * observe the user's favorite [UserArticle]
     * */
    fun observeArticleFavorite(): Flow<List<UserArticle>>

    fun observeSearchArticle(query: String, apiKey: String): Flow<List<UserArticle>>
}
