package com.rick.data_movie.imdb.search_model

import androidx.room.Entity

@Entity(tableName = "searched_movie")
data class SearchedMovie(
    val description: String,
    val id: String,
    val image: String,
    val resultType: String,
    val title: String
)