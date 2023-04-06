package com.rick.data_movie.favorite

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "favorite")
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Long,
    val title: String,
    val rating: String,
    val summary: String,
    val favorite: Boolean
)
