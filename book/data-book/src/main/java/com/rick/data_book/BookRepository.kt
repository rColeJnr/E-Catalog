package com.rick.data_book

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.data_book.model.Book
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val BOOKS_PER_PAGE = 32

class BookRepository @Inject constructor(
    private val db: BookDatabase,
    private val api: GutenbergApi
) {

    fun getBooks(): Flow<PagingData<Book>> {
        val pagingSourceFactory = { db.bookDao.getBooks() }
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = BOOKS_PER_PAGE,
                enablePlaceholders = true,
                prefetchDistance = 1,
                initialLoadSize = 1
            ),
            remoteMediator = BookRemoteMediator(api = api, db = db),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

}