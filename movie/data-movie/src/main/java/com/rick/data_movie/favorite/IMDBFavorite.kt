package com.rick.data_movie.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "imdb_favorite")
data class IMDBFavorite(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val title: String,
    val type: String,
    val image: String,
    val plot: String,
)
