package com.rick.data_book.nytimes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ny_book_remote_keys")
data class NYBookRemoteKeys(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val prevKey: Int?,
    val nextKey: Int?
)
