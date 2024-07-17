package com.rick.data.translation

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface TranslationNetworkModule {

    @Binds
    fun bindTranslationDataSource(
        impl: DemoTranslationNetworkDataSource
    ): TranslationDataSource
}