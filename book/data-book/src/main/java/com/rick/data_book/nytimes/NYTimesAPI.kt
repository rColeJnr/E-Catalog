package com.rick.data_book.nytimes

import com.rick.data_book.nytimes.model.NYTimesResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface NYTimesAPI {

    @GET("svc/books/v3/lists/{date}/{list}.json")
    fun getBestsellers(
        @Path("date") date: String,
        @Path("list") bookGenre: String
    ): NYTimesResponse

    fun getBook(

    )
    companion object {
        private const val BASE_URL = "https://api.nytimes.com/"
    }

}