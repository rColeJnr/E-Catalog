package com.rick.ecatalogs.game.data_game.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rick.ecatalogs.game.data_game.model.Game

@Entity(tableName = "game_table")
data class GameEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    @ColumnInfo("cover_image_id")
    val coverImageId: String?,
    val summary: String,
    val rating: Double?,
    @ColumnInfo("rating_count")
    val ratingCount: Int,
    /** Epoch seconds */
    @ColumnInfo("release_date")
    val releaseDate: Long?,
    val genres: List<String>,
    val platforms: List<String>,
    /** Place in the catalog list (popularity order); null for rows kept only as search results or favorites. */
    @ColumnInfo("catalog_position")
    val catalogPosition: Int? = null,
)

fun GameEntity.asGame() = Game(
    id = id,
    name = name,
    coverImageId = coverImageId,
    summary = summary,
    rating = rating,
    ratingCount = ratingCount,
    releaseDate = releaseDate,
    genres = genres,
    platforms = platforms,
)

fun Game.asEntity(catalogPosition: Int? = null) = GameEntity(
    id = id,
    name = name,
    coverImageId = coverImageId,
    summary = summary,
    rating = rating,
    ratingCount = ratingCount,
    releaseDate = releaseDate,
    genres = genres,
    platforms = platforms,
    catalogPosition = catalogPosition,
)
