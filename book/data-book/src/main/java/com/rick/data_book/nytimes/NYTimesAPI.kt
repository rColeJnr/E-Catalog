package com.rick.data_book.nytimes

import com.rick.data_book.nytimes.model.NYTimesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NYTimesAPI {

    @GET("svc/books/v3/lists/{date}/{list}.json")
    fun getBestsellers(
        @Path("date") date: String = DATE,
        @Path("list") bookGenre: String,
        @Query("api-key") apiKey: String,
        @Query("page") page: Int
    ): NYTimesResponse

    companion object {
        const val BASE_URL = "https://api.nytimes.com/"
        private const val DATE = "current"
    }

}