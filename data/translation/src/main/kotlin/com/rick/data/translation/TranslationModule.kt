package com.rick.data.translation

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TranslationModule {

    @Binds
    internal abstract fun bindsTranslationRepository(
        translationRepository: TranslationRepositoryImpl
    ): TranslationRepository
}