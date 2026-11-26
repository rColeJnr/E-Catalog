package com.rick.ecatalogs.game.data_game.network.di

import android.content.Context
import com.rick.ecatalogs.game.data_game.network.demo.DemoIgdbAssetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    fun providesDemoIgdbAssetManager(
        @ApplicationContext context: Context,
    ): DemoIgdbAssetManager = DemoIgdbAssetManager(context.assets::open)
}
