package com.rick.data.database_movie.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article_remote_keys_table")
data class ArticleRemoteKeys(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo("prev_key")
    val prevKey: Int?,
    @ColumnInfo("next_key")
    val nextKey: Int?
)
