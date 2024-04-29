package com.rick.data.data_book.repository.gutenberg

import androidx.paging.PagingData
import com.rick.data.model_book.gutenberg.UserGutenberg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface UserGutenbergRepository {

    fun observeGutenberg(scope: CoroutineScope): Flow<PagingData<UserGutenberg>>

    /**
     * observe the user's favorite anime
     * */
    fun observeGutenbergFavorite(): Flow<List<UserGutenberg>>

    fun searchGutenbergs(query: String): Flow<List<UserGutenberg>>
}