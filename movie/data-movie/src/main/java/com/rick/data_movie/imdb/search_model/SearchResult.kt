package com.rick.data_movie.imdb.search_model

import androidx.room.Entity

@Entity(tableName = "search_result")
data class SearchResult(
    val description: String,
    val id: String,
    val image: String,
    val resultType: String,
    val title: String
)