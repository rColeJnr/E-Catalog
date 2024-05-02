package com.rick.book.data_book.network.di

import android.content.Context
import com.rick.book.data_book.network.demo.DemoBookAssetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun providesNetworkBookJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun providesDemoAssetManager(
        @ApplicationContext context: Context
    ): DemoBookAssetManager = DemoBookAssetManager(context.assets::open)


}