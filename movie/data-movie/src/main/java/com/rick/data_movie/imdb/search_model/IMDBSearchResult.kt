package com.rick.data_movie.imdb.search_model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "imdb_search_result")
@Parcelize
data class IMDBSearchResult(
    @field:SerializedName("id")
    @PrimaryKey (autoGenerate = false) val id: String,
    @field:SerializedName("resultType")
    val resultType: String,
    @field:SerializedName("image")
    val image: String,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("description")
    val description: String
) : Parcelable