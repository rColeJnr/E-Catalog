package com.rick.data_book

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.rick.core.Resource
import com.rick.data.model_book.Favorite
import com.rick.data_book.gutenberg.BookRemoteMediator
import com.rick.data_book.gutenberg.GutenbergApi
import com.rick.data_book.gutenberg.model.Book
import com.rick.data_book.gutenberg.model.toGutenBergResponse
import com.rick.data_book.nytimes.NYBookRemoteMediator
import com.rick.data_book.nytimes.NYTimesAPI
import com.rick.data_book.nytimes.model.NYBook
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val BOOKS_PER_PAGE = 15

class BookRepository @Inject constructor(
    private val db: BookDatabase,
    private val api: GutenbergApi,
    private val nyApi: NYTimesAPI
) {

    fun getBooks(): Flow<PagingData<Book>> {
        val pagingSourceFactory = { db.bookDao.getBooks() }
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = BOOKS_PER_PAGE,
                enablePlaceholders = true,
                prefetchDistance = 0,
                initialLoadSize = 2
            ),
            remoteMediator = BookRemoteMediator(api = api, db = db),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun getBestsellers(apiKey: String, bookGenre: String): Flow<PagingData<NYBook>> {
        val pagingSourceFactory = { db.bookDao.getBestsellers() }
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = BOOKS_PER_PAGE,
                enablePlaceholders = true,
                prefetchDistance = 0,
                initialLoadSize = 2
            ),
            remoteMediator = NYBookRemoteMediator(
                api = nyApi,
                db = db,
                key = apiKey,
                bookGenre = bookGenre
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

//    fun getBestsellers(apikey: String, bookGenre: String): Flow<Resource<List<NYBook>>> {
//        var books: List<NYBook> = listOf()
//        // appending '%' so we can allow other characters to be before and after the query string
//        return flow {
//
//            emit(Resource.Loading(true))
//
//            db.withTransaction {
//                books = db.bookDao.getBestsellers()
//            }
//
//            if (books.isNotEmpty()) {
//                emit(Resource.Success<List<NYBook>>(data = books))
//                emit(Resource.Loading(false))
//            }
//
//            try {
//                val response = nyApi.getBestsellers(
//                    page = 1,
//                    apiKey = apikey,
//                    bookGenre = bookGenre
//                )
//                if (response.results.books.isNotEmpty()) {
//                    db.withTransaction {
//                        db.bookDao.insertBestsellers(response.results.books)
//                        books = db.bookDao.getBestsellers()
//                    }
//                    emit(Resource.Success<List<NYBook>>(data = books))
//                    emit(Resource.Loading(false))
//                } else {
//                    emit(Resource.Error(message = null))
//                    emit(Resource.Loading(false))
//                }
//            } catch (e: IOException) {
//                emit(Resource.Error(e.message))
//                emit(Resource.Loading(false))
//            } catch (e: HttpException) {
//                emit(Resource.Error(e.message))
//                emit(Resource.Loading(false))
//            }
//        }
//
//    }

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

    suspend fun getFavoriteBook(): Flow<Resource<List<com.rick.data.model_book.Favorite>>> {
        return flow {

            emit(Resource.Loading(true))

            val favorites: List<com.rick.data.model_book.Favorite> = db.favDao.getFavorites()

            emit(Resource.Loading(false))
            emit(Resource.Success(favorites))

        }
    }

    suspend fun insert(favorite: com.rick.data.model_book.Favorite) {
        db.favDao.insert(favorite)
    }

    suspend fun delete(favorite: com.rick.data.model_book.Favorite) {
        db.favDao.delete(favorite)
    }


}