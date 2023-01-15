package com.rick.data_anime

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.rick.core.GsonParser
import com.rick.data_anime.JikanApi.Companion.JIKAN_BASE_URL
import com.rick.data_anime.JikanDatabase.Companion.JIKAN_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

@InstallIn(SingletonComponent::class)
@Module
object JikanDIModule {

    @Provides
    fun providesJikanApi(): JikanApi = Retrofit.Builder()
        .baseUrl(JIKAN_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build()
        )
        .build()
        .create()

    @Provides
    fun provideJikanDatabase(@ApplicationContext context: Context): JikanDatabase =
        Room.databaseBuilder(
            context,
            JikanDatabase::class.java,
            JIKAN_DATABASE_NAME
        ).addTypeConverter(JikanConverters(GsonParser(Gson())))
            .build()

    @Provides
    fun provideJikanRepository(api: JikanApi, db: JikanDatabase): JikanRepository =
        JikanRepository(api, db)

}