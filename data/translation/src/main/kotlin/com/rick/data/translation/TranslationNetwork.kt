package com.rick.data.translation

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import javax.inject.Inject
import javax.inject.Singleton

interface TranslationApi {

    @Headers(
        "Content-Type: application/json",
    )
    @POST("translate/v2/translate")
    suspend fun getTranslation(
        @Header("Authorization") apiKey: String,
        @Body translationBody: TranslationBody
    ): TranslationResponse

}

@Singleton
internal class TranslationNetwork @Inject constructor() : TranslationDataSource {
    private val apiKey = BuildConfig.TranslationApiKey
    private val api = Retrofit.Builder().baseUrl("https://translate.api.cloud.yandex.net/")
        .addConverterFactory(
            GsonConverterFactory
                .create()
        )
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build()
        )
        .build()
        .create(TranslationApi::class.java)

    override suspend fun getTranslation(body: TranslationBody): TranslationResponse =
        api.getTranslation(apiKey = apiKey, translationBody = body)
}