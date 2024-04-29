package com.rick.data.network_anime.di

import android.content.Context
import com.rick.data.network_anime.demo.DemoJikanAssetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

//    @Provides
//    @Singleton
//    fun providesNetworkJikanJson(): Json = Json {
//        ignoreUnknownKeys = true
//    }

    @Provides
    @Singleton
    fun providesDemoJikanAssetManager(
        @ApplicationContext context: Context
    ): DemoJikanAssetManager = DemoJikanAssetManager(context.assets::open)

}