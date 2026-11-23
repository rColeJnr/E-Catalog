package com.rick.ecatalogs.movie.data_movie.data.repository.article

import androidx.paging.PagingData
import com.rick.ecatalogs.movie.data_movie.model.UserArticle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface UserArticlesRepository {

    fun observeArticle(
        viewModelScope: CoroutineScope
    ): Flow<PagingData<UserArticle>>

    /**
     * observe the user's favorite [UserArticle]
     * */
    fun observeArticleFavorite(): Flow<List<UserArticle>>

    fun observeSearchArticle(query: String): Flow<List<UserArticle>>
}
