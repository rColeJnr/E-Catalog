package com.rick.settings.data_settings.data.di

import com.rick.settings.data_settings.data.repository.UserSettingsDataRepository
import com.rick.settings.data_settings.data.repository.UserSettingsDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindUserSettingsDataRepository(
        impl: UserSettingsDataRepositoryImpl
    ): UserSettingsDataRepository
}