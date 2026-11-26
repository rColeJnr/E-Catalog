package com.rick.ecatalogs.game.data_game.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_remote_keys")
data class GameRemoteKeys(
    @PrimaryKey val game: Int,
    val prevKey: Int?,
    val nextKey: Int?,
)
