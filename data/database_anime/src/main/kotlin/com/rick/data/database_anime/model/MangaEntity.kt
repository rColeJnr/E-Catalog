package com.rick.data.database_anime.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rick.data.model_anime.Manga
import com.rick.data.model_anime.model_jikan.Author
import com.rick.data.model_anime.model_jikan.Genre
import com.rick.data.model_anime.model_jikan.Published
import com.rick.data.model_anime.model_jikan.Serialization
import com.rick.data.model_anime.model_jikan.Theme

@Entity(tableName = "manga_table")
data class MangaEntity(
    @PrimaryKey
    val id: Int,
    val url: String,
    val image: String,
    val title: String,
    val titleEnglish: String,
    val type: String,
    val chapters: Int,
    val volumes: Int,
    val publishing: Boolean,
    val published: Published,
    val score: Double,
    val scoredBy: Int,
    val rank: Int,
    val popularity: Int,
    val favorites: Int,
    val synopsis: String,
    val background: String,
    val authors: List<Author>,
    val serializations: List<Serialization>,
    val genres: List<Genre>,
    val themes: List<Theme>,
)

fun MangaEntity.asManga() = Manga(
    id = id,
    url = url,
    images = image,
    title = title,
    titleEnglish = titleEnglish,
    type = type,
    chapters = chapters,
    volumes = volumes,
    publishing = publishing,
    published = published,
    score = score,
    scoredBy = scoredBy,
    rank = rank,
    popularity = popularity,
    favorites = favorites,
    synopsis = synopsis,
    background = background,
    authors = authors,
    serializations = serializations,
    genres = genres,
    themes = themes
)