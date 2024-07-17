package com.rick.data.translation

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TranslationModule {

    @Binds
    internal abstract fun bindsTranslationRepository(
        translationRepository: TranslationRepositoryImpl
    ): TranslationRepository

}

@Module
@InstallIn(SingletonComponent::class)
internal object DemoTranslationModule {
    @Provides
    @Singleton
    fun providesDemoTranslationAssetManager(
        @ApplicationContext context: Context
    ): DemoTranslationAssetManager = DemoTranslationAssetManager(context.assets::open)
}