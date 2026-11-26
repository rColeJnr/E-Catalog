package com.rick.ecatalogs.game.data_game.database.di

import android.content.Context
import androidx.room.Room
import com.rick.ecatalogs.game.data_game.database.GameDatabase
import com.rick.ecatalogs.game.data_game.database.dao.GameDao
import com.rick.ecatalogs.game.data_game.database.dao.GameRecentSearchQueryDao
import com.rick.ecatalogs.game.data_game.database.dao.GameRemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesGameDatabase(
        @ApplicationContext context: Context,
    ): GameDatabase = Room.databaseBuilder(context, GameDatabase::class.java, "igdb-database").build()

    @Provides
    fun providesGameDao(db: GameDatabase): GameDao = db.gameDao

    @Provides
    fun providesGameRemoteKeysDao(db: GameDatabase): GameRemoteKeysDao = db.gameRemoteKeysDao

    @Provides
    fun providesGameRecentSearchQueryDao(db: GameDatabase): GameRecentSearchQueryDao =
        db.gameRecentSearchQueryDao
}
