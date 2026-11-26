package com.rick.ecatalogs.game.data_game.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rick.ecatalogs.game.data_game.database.dao.GameDao
import com.rick.ecatalogs.game.data_game.database.dao.GameRecentSearchQueryDao
import com.rick.ecatalogs.game.data_game.database.dao.GameRemoteKeysDao
import com.rick.ecatalogs.game.data_game.database.model.GameEntity
import com.rick.ecatalogs.game.data_game.database.model.GameRecentSearchQueryEntity
import com.rick.ecatalogs.game.data_game.database.model.GameRemoteKeys
import com.rick.ecatalogs.game.data_game.database.util.GameConverters

@Database(
    entities = [
        GameEntity::class,
        GameRemoteKeys::class,
        GameRecentSearchQueryEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(GameConverters::class)
abstract class GameDatabase : RoomDatabase() {
    abstract val gameDao: GameDao
    abstract val gameRemoteKeysDao: GameRemoteKeysDao
    abstract val gameRecentSearchQueryDao: GameRecentSearchQueryDao
}
