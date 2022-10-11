package com.rick.data_book

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class BookDto(
    @SerializedName("authors")
    val authors: List<Author>,
    @SerializedName("bookshelves")
    val bookshelves: List<String>,
    @SerializedName("copyright")
    val copyright: Boolean,
    @SerializedName("download_count")
    val downloadCount: Int,
    @SerializedName("formats")
    val formats: FormatsDto,
    @SerializedName("id")
    val id: Int,
    @SerializedName("languages")
    val languages: List<String>,
    @SerializedName("media_type")
    val mediaType: String,
    @SerializedName("subjects")
    val subjects: List<String>,
    @SerializedName("title")
    val title: String,
    @SerializedName("translators")
    val translators: List<TranslatorDto>
)

@Parcelize
@Entity (tableName = "book_db")
data class Book(
    @PrimaryKey (autoGenerate = false)
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("authors")
    val authors: List<Author>,
    @SerializedName("bookshelves")
    val bookshelves: List<String>,
    @SerializedName("copyright")
    val copyright: Boolean,
    @SerializedName("download_count")
    val downloads: Int,
    @SerializedName("formats")
    val formats: Formats,
    @SerializedName("languages")
    val languages: List<String>,
    @SerializedName("media_type")
    val mediaType: String,
    @SerializedName("subjects")
    val subjects: List<String>,
    @SerializedName("translators")
    val translators: List<Translator>
): Parcelable