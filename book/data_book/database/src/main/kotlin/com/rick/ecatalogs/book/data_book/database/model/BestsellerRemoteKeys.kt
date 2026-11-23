package com.rick.ecatalogs.book.data_book.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ny_book_remote_keys")
data class BestsellerRemoteKeys(
    @PrimaryKey
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)
