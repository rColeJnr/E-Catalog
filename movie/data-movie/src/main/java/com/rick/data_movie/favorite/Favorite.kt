package com.rick.data_movie.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "favorite")
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Int?,
    val title: String,
    val overview: String,
    val image: String,
    val type: String,
)
