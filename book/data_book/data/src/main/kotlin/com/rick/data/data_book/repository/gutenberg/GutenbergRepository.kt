package com.rick.data.data_book.repository.gutenberg

import androidx.paging.PagingData
import com.rick.data.database_book.model.GutenbergEntity
import com.rick.data.model_book.gutenberg.Gutenberg
import com.rick.data.model_book.gutenberg.UserGutenberg
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [UserGutenberg]
 * */

interface GutenbergRepository {

    fun getGutenberg(): Flow<PagingData<GutenbergEntity>>

    fun getGutenbergFavorites(favorites: Set<Int>): Flow<List<Gutenberg>>

    fun searchGutenberg(query: String): Flow<List<Gutenberg>>
}