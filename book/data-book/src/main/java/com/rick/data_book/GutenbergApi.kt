package com.rick.data_book

import com.rick.data_book.model.GutenbergResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GutenbergApi {
    @GET("/books")
    suspend fun fetchBooks(
        @Query("page") page: Int
    ): GutenbergResponseDto

    @GET("/books/")
    suspend fun getBook(
        @Query("id") id: Int
    ): GutenbergResponseDto

    @GET("/books/")
    suspend fun searchBook(
        @Query("search") query: String
    ): GutenbergResponseDto

    companion object {
        const val GUTENBERG_BASE_URL = "https://gutendex.com/"
    }
}