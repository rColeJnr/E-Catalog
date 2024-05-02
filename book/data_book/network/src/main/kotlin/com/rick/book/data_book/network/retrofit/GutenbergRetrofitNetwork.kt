package com.rick.book.data_book.network.retrofit

import com.rick.book.data_book.network.GutenbergNetworkDataSource
import com.rick.book.data_book.network.model.GutenbergResponse
import com.rick.book.data_book.network.retrofit.GutenbergRetrofitNetworkApi.Companion.GUTENBERG_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

private interface GutenbergRetrofitNetworkApi {

    @GET("/books")
    suspend fun fetchBooks(
        @Query("page") page: Int
    ): GutenbergResponse

    @GET("/books/")
    suspend fun searchBook(
        @Query("search") query: String
    ): GutenbergResponse

    companion object {
        const val GUTENBERG_BASE_URL = "https://gutendex.com/"
    }

}

@Singleton
internal class GutenbergRetrofitNetwork @Inject constructor() : GutenbergNetworkDataSource {
    private val networkApi = Retrofit.Builder().baseUrl(GUTENBERG_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build()
        ).build().create(GutenbergRetrofitNetworkApi::class.java)

    override suspend fun fetchBooks(page: Int): GutenbergResponse = networkApi.fetchBooks(page)

    override suspend fun searchGutenberg(query: String): GutenbergResponse =
        networkApi.searchBook(query)
}