package com.rick.ecatalogs.movie.data_movie.network.retrofit

import com.rick.ecatalogs.movie.data_movie.network.ArticleNetworkDataSource
import com.rick.ecatalogs.movie.data_movie.network.BuildConfig
import com.rick.ecatalogs.movie.data_movie.network.model.ArticleResponse
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
    ): ArticleResponse

    @GET("svc/search/v2/articlesearch.json?")
    suspend fun searchMovieArticles(
        @Query("fq") section_name: String,
        @Query("sort") sort: String,
        @Query("q") query: String,
    ): ArticleResponse


}

@Singleton
internal class NyTimesRetrofitNetwork @Inject constructor() : ArticleNetworkDataSource {
    private val networkApi = Retrofit.Builder()
        .baseUrl("https://api.nytimes.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(ApiKeyInterceptor("api-key", BuildConfig.NY_TIMES_API_KEY))
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build()
        )
        .build()
        .create(NyTimesRetrofitNetworkApi::class.java)

    override suspend fun fetchMovieArticles(
        page: Int,
        section_name: String,
        sort: String
    ): ArticleResponse =
        networkApi.fetchMovieArticles(
            sort = "newest",
            page = page,
            section_name = "section.name:\"movies\" AND typeOfMaterials:\"review\""
        )

    override suspend fun searchMovieArticles(
        query: String,
        section_name: String,
        sort: String,
    ): ArticleResponse =
        networkApi.searchMovieArticles(
            query = query,
            section_name = "section.name:\"movies\" AND typeOfMaterials:\"review\"",
            sort = "relevance",
        )
}
