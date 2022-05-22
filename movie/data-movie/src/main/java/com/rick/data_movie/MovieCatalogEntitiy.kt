package com.rick.data_movie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MovieCatalogEntitiy(
    @PrimaryKey val id: Int? = null,
    val movieCatalog: List<Result>,
    val hasMore: Boolean
)
