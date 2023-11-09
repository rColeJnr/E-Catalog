package com.rick.data_book

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.rick.core.GsonParser
import com.rick.data_book.BookDatabase.Companion.DATABASE_NAME
import com.rick.data_book.BookDatabase.Companion.MIGRATION_1_2
import com.rick.data_book.gutenberg.GutenbergApi
import com.rick.data_book.gutenberg.GutenbergApi.Companion.GUTENBERG_BASE_URL
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
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object BookDIModule {

    @Provides
    @Singleton
    fun provideGutenbergApi(): GutenbergApi = Retrofit
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
    @Singleton
    fun providesBookDatabase(@ApplicationContext context: Context): BookDatabase =
        Room.databaseBuilder(
            context,
            BookDatabase::class.java,
            DATABASE_NAME,
        ).addTypeConverter(BookTypeConverters(GsonParser(Gson())))
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    @Singleton
    fun provideBookRepository(db: BookDatabase, api: GutenbergApi) : BookRepository =
        BookRepository(db, api)
}