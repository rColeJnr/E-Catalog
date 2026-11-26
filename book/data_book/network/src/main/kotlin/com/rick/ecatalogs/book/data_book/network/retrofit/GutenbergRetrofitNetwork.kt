package com.rick.ecatalogs.book.data_book.network.retrofit

import com.rick.ecatalogs.book.data_book.network.BuildConfig
import com.rick.ecatalogs.book.data_book.network.GutenbergNetworkDataSource
import com.rick.ecatalogs.book.data_book.network.model.GutenbergResponse
import com.rick.ecatalogs.book.data_book.network.retrofit.GutenbergRetrofitNetworkApi.Companion.GUTENBERG_BASE_URL
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
        @Query("page") page: Int,
        // Retrofit leaves null query parameters out
        @Query("languages") languages: String?,
        @Query("topic") topic: String?,
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
                .addInterceptor(
                    // URLs carry API keys: log them only in debug builds
                    HttpLoggingInterceptor().setLevel(
                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE,
                    ),
                )
                .build()
        ).build().create(GutenbergRetrofitNetworkApi::class.java)

    override suspend fun fetchBooks(page: Int, languages: String?, topic: String?): GutenbergResponse =
        networkApi.fetchBooks(page, languages, topic)

    override suspend fun searchGutenberg(query: String): GutenbergResponse =
        networkApi.searchBook(query)
}