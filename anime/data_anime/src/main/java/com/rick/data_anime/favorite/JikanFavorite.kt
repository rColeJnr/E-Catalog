package com.rick.data_anime.favorite

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "favorite")
data class JikanFavorite(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    val id: Long,
    val title: String,
    val image: String,
    val synopsis: String,
    val type: String,
    val isFavorite: Boolean
) : Parcelable
