package com.rick.data.network_book.retrofit

import com.rick.data.network_book.BestsellerNetworkDataSource
import com.rick.data.network_book.model.BestsellerResponse
import com.rick.data.network_book.model.SearchBestsellerResponse
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

    @GET("svc/books/v3/lists/{date}/{list}.json")
    suspend fun getBestsellers(
        @Path("date") date: String,
        @Path("list") bookGenre: String,
        @Query("api-key") apiKey: String,
    ): BestsellerResponse

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
        date: String, bookGenre: String, apiKey: String
    ): BestsellerResponse =
        networkApi.getBestsellers(date = date, bookGenre = bookGenre, apiKey = apiKey)

    override suspend fun searchBestseller(query: String, apiKey: String): SearchBestsellerResponse {
        TODO("Not yet implemented")
    }
}