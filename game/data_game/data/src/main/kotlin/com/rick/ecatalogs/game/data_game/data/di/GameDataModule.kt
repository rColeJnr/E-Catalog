package com.rick.ecatalogs.game.data_game.data.di

import com.rick.ecatalogs.game.data_game.data.repository.CompositeGameRepository
import com.rick.ecatalogs.game.data_game.data.repository.GameBrowseRepository
import com.rick.ecatalogs.game.data_game.data.repository.GameBrowseRepositoryImpl
import com.rick.ecatalogs.game.data_game.data.repository.GameRecentSearchRepository
import com.rick.ecatalogs.game.data_game.data.repository.GameRecentSearchRepositoryImpl
import com.rick.ecatalogs.game.data_game.data.repository.GameRepository
import com.rick.ecatalogs.game.data_game.data.repository.GameRepositoryImpl
import com.rick.ecatalogs.game.data_game.data.repository.UserGameDataRepository
import com.rick.ecatalogs.game.data_game.data.repository.UserGameDataRepositoryImpl
import com.rick.ecatalogs.game.data_game.data.repository.UserGameRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface GameDataModule {
    @Binds
    fun bindsGameRepository(impl: GameRepositoryImpl): GameRepository

    @Binds
    fun bindsUserGameDataRepository(impl: UserGameDataRepositoryImpl): UserGameDataRepository

    @Binds
    fun bindsUserGameRepository(impl: CompositeGameRepository): UserGameRepository

    @Binds
    fun bindsGameRecentSearchRepository(impl: GameRecentSearchRepositoryImpl): GameRecentSearchRepository

    @Binds
    fun bindsGameBrowseRepository(impl: GameBrowseRepositoryImpl): GameBrowseRepository
}
