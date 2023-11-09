package com.rick.data_book

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.rick.core.Resource
import com.rick.data_book.favorite.Favorite
import com.rick.data_book.gutenberg.BookRemoteMediator
import com.rick.data_book.gutenberg.GutenbergApi
import com.rick.data_book.gutenberg.model.Book
import com.rick.data_book.gutenberg.model.toGutenBergResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val BOOKS_PER_PAGE = 2

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
            ),
            remoteMediator = BookRemoteMediator(api = api, db = db),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun searchBooks(query: String): Flow<Resource<List<Book>>> {
        var books: List<Book> = listOf()
        // appending '%' so we can allow other characters to be before and after the query string
        val dbQuery = "%${query.replace(' ', '%')}%"
        return flow {

            emit(Resource.Loading(true))

            db.withTransaction {
                books = db.bookDao.getBook(dbQuery)
            }

            if (books.isNotEmpty()) {
                emit(Resource.Success<List<Book>>(data = books))
                emit(Resource.Loading(false))
            }

            try {
                val response = api.searchBook(query).toGutenBergResponse()
                if (response.books.isNotEmpty()) {
                    db.withTransaction {
                        db.bookDao.insertBooks(response.books)
                        books = db.bookDao.getBook(dbQuery)
                    }
                    emit(Resource.Success<List<Book>>(data = books))
                    emit(Resource.Loading(false))
                } else {
                    emit(Resource.Error(message = null))
                    emit(Resource.Loading(false))
                }
            } catch (e: IOException) {
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
            } catch (e: HttpException) {
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
            }
        }

    }

    suspend fun getFavoriteBook(): Flow<Resource<List<Favorite>>> {
        return flow {

            emit(Resource.Loading(true))

            val favorites: List<Favorite> = db.favDao.getFavorites()

            emit(Resource.Loading(false))
            emit(Resource.Success(favorites))

        }
    }

    suspend fun insert(favorite: Favorite) {
        db.favDao.insert(favorite)
    }

    suspend fun delete(favorite: Favorite) {
        db.favDao.delete(favorite)
    }



}