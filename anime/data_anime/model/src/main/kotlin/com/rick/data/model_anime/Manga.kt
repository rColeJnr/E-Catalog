package com.rick.data.model_anime

import com.rick.data.model_anime.model_jikan.Author
import com.rick.data.model_anime.model_jikan.Genre
import com.rick.data.model_anime.model_jikan.Published
import com.rick.data.model_anime.model_jikan.Serialization
import com.rick.data.model_anime.model_jikan.Theme
import kotlinx.serialization.Serializable

@Serializable
data class Manga(
    val id: Int,
    val url: String,
    val images: String,
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
