package com.rick.ecatalogs.data.translation

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface TranslationNetworkModule {

    @Binds
    fun bindTranslationNetwork(
        impl: TranslationNetwork
    ): TranslationDataSource
}