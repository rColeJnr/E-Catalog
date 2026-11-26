package com.rick.ecatalogs.game.data_game.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rick.ecatalogs.game.data_game.model.GameRecentSearchQuery
import kotlinx.datetime.Instant

@Entity(tableName = "game_recent_search_queries_table")
data class GameRecentSearchQueryEntity(
    @PrimaryKey
    val query: String,
    val queriedDate: Instant,
)

fun GameRecentSearchQueryEntity.asExternalModel() = GameRecentSearchQuery(query, queriedDate)
