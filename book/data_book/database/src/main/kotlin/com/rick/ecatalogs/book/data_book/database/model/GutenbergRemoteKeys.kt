package com.rick.ecatalogs.book.data_book.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_remote_keys")
data class GutenbergRemoteKeys(
    @PrimaryKey val book: Int,
    val prevKey: Int?,
    val nextKey: Int?
)