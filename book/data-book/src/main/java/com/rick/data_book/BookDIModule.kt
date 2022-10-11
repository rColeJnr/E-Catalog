package com.rick.data_book

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.gson.Gson
import com.rick.core.GsonParser
import com.rick.data_book.BookDatabase.Companion.DATABASE_NAME
import com.rick.data_book.GutenbergApi.Companion.GUTENBERG_BASE_URL
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
object BookDIModule {

    @Provides
    fun provideGutenbergApi(): Retrofit = Retrofit
        .Builder()
        .baseUrl(GUTENBERG_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            ).build())
        .build()
        .create()

    @Provides
    fun providesBookDatabase(@ApplicationContext context: Context): RoomDatabase =
        Room.databaseBuilder(
            context,
            BookDatabase::class.java,
            DATABASE_NAME,
        ).addTypeConverter(BookTypeConverters(GsonParser(Gson())))
            .build()

    @Provides
    fun provideBookRepository(db: BookDatabase, api: GutenbergApi) : BookRepository =
        BookRepository(db, api)
}