package com.rick.screen_book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.data_book.BookRepository
import com.rick.data_book.model.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class BookCatalogViewModel @Inject constructor(
    private val repository: BookRepository
): ViewModel() {

    val pagingDataFlow: Flow<PagingData<Book>>

    init {

        pagingDataFlow = fetchBooks().cachedIn(viewModelScope)
    }

    private fun fetchBooks(): Flow<PagingData<Book>> =
        repository.getBooks()

}