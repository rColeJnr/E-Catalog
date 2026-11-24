package com.rick.ecatalogs.book.data_book.data.repository.gutenberg

import com.rick.ecatalogs.book.data_book.model.gutenberg.GutenbergFilter
import androidx.paging.PagingData
import com.rick.ecatalogs.book.data_book.model.gutenberg.UserGutenberg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface UserGutenbergRepository {

    fun observeGutenberg(
        scope: CoroutineScope,
        filter: GutenbergFilter = GutenbergFilter(),
    ): Flow<PagingData<UserGutenberg>>

    /**
     * observe the user's favorite anime
     * */
    fun observeGutenbergFavorite(): Flow<List<UserGutenberg>>

    fun searchGutenbergs(query: String): Flow<List<UserGutenberg>>
}