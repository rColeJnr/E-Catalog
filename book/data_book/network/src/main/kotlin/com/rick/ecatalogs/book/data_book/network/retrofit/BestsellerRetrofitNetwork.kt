package com.rick.ecatalogs.book.data_book.network.retrofit

import com.rick.ecatalogs.book.data_book.network.BestsellerNetworkDataSource
import com.rick.ecatalogs.book.data_book.network.BuildConfig
import com.rick.ecatalogs.book.data_book.network.model.BestsellerOverviewResponse
import com.rick.ecatalogs.book.data_book.network.model.BestsellerResponse
import com.rick.ecatalogs.book.data_book.network.model.BookReviewsResponse
import com.rick.ecatalogs.book.data_book.network.model.ReviewArticleResponse
import com.rick.ecatalogs.book.data_book.network.model.SearchBestsellerResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

private interface NyTimesRetrofitNetworkApi {

    /*
        Fetch NYTimes list service to get one specific Best Sellers list for a given week
        or month depending on the list type.
     */
    @GET("svc/books/v3/lists/{date}/{list}.json")
    suspend fun getBestsellers(
        @Path("date") date: String,
        @Path("list") bookGenre: String,
        @Query("offset") offset: Int,
        @Query("api-key") apiKey: String,
    ): BestsellerResponse

    /* Top entries of every Best Sellers list for the current week, in one request. */
    @GET("svc/books/v3/lists/overview.json")
    suspend fun getOverview(
        @Query("api-key") apiKey: String,
    ): BestsellerOverviewResponse

    @GET("svc/books/v3/reviews.json")
    suspend fun getReviews(
        @Query("isbn") isbn: String,
        @Query("api-key") apiKey: String,
    ): BookReviewsResponse

    @GET("svc/search/v2/articlesearch.json")
    suspend fun getArticle(
        @Query("fq") filter: String,
        @Query("api-key") apiKey: String,
    ): ReviewArticleResponse

    companion object {
        private const val DATE = "current"
    }

}

@Singleton
internal class BestsellerRetrofitNetwork @Inject constructor() : BestsellerNetworkDataSource {
    private val networkApi = Retrofit.Builder().baseUrl("https://api.nytimes.com/")
        .addConverterFactory(GsonConverterFactory.create()).client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build()
        ).build().create(NyTimesRetrofitNetworkApi::class.java)

    override suspend fun fetchBestsellers(
        date: String, bookGenre: String, offset: Int,
    ): BestsellerResponse =
        networkApi.getBestsellers(
            date = date,
            bookGenre = bookGenre,
            offset = offset,
            apiKey = BuildConfig.NY_TIMES_API_KEY,
        )

    override suspend fun fetchOverview(): BestsellerOverviewResponse =
        networkApi.getOverview(apiKey = BuildConfig.NY_TIMES_API_KEY)

    override suspend fun fetchReviews(isbn: String): BookReviewsResponse =
        networkApi.getReviews(isbn = isbn, apiKey = BuildConfig.NY_TIMES_API_KEY)

    override suspend fun fetchArticle(url: String): ReviewArticleResponse =
        networkApi.getArticle(
            filter = "web_url:(\"${url.replace("\"", "")}\")",
            apiKey = BuildConfig.NY_TIMES_API_KEY,
        )

    override suspend fun searchBestseller(query: String): SearchBestsellerResponse {
        TODO("Not yet implemented")
    }
}