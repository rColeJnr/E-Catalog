package com.rick.ecatalogs.book.data_book.data.repository.gutenberg

import com.rick.ecatalogs.book.data_book.model.gutenberg.GutenbergFilter
import androidx.paging.PagingData
import com.rick.ecatalogs.book.data_book.database.model.GutenbergEntity
import com.rick.ecatalogs.book.data_book.model.gutenberg.Gutenberg
import com.rick.ecatalogs.book.data_book.model.gutenberg.UserGutenberg
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [UserGutenberg]
 * */

interface GutenbergRepository {

    fun getGutenberg(filter: GutenbergFilter = GutenbergFilter()): Flow<PagingData<GutenbergEntity>>

    fun getGutenbergFavorites(favorites: Set<Int>): Flow<List<Gutenberg>>

    fun searchGutenberg(query: String): Flow<List<Gutenberg>>
}