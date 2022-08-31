package com.rick.data_movie.imdb.search_model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "imdb_search_result")
data class IMDBSearchResult(
    @PrimaryKey (autoGenerate = false)
    val id: String,
    val searchType: String,
    val description: String,
    val image: String,
    val resultType: String,
    val title: String
)