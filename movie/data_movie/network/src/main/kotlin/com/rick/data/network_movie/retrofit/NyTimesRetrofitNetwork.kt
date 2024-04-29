package com.rick.data.network_movie.retrofit

import com.rick.data.network_movie.ArticleNetworkDataSource
import com.rick.data.network_movie.model.ArticleResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

private interface NyTimesRetrofitNetworkApi {

    @GET("svc/search/v2/articlesearch.json?")
    suspend fun fetchMovieArticles(
        @Query("fq") section_name: String,
        @Query("sort") sort: String,
        @Query("page") page: Int,
        @Query("api-key") apikey: String
    ): ArticleResponse

    @GET("svc/search/v2/articlesearch.json?")
    suspend fun searchMovieArticles(
        @Query("fq") section_name: String,
        @Query("sort") sort: String,
        @Query("q") query: String,
        @Query("api-key") apikey: String
    ): ArticleResponse


}

@Singleton
internal class NyTimesRetrofitNetwork @Inject constructor() : ArticleNetworkDataSource {
    private val networkApi = Retrofit.Builder()
        .baseUrl("https://api.nytimes.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build()
        )
        .build()
        .create(NyTimesRetrofitNetworkApi::class.java)

    override suspend fun fetchMovieArticles(
        page: Int,
        apikey: String,
        section_name: String,
        sort: String
    ): ArticleResponse =
        networkApi.fetchMovieArticles(
            sort = "newest",
            page = page,
            apikey = apikey,
            section_name = "section_name:\"Movies\" AND type_of_material:\"Review\""
        )

    override suspend fun searchMovieArticles(
        apiKey: String,
        query: String,
        section_name: String,
        sort: String,
    ): ArticleResponse =
        networkApi.searchMovieArticles(
            query = query,
            apikey = apiKey,
            section_name = "section_name:\"Movies\" AND type_of_material:\"Review\"",
            sort = "relevance",
        )
}