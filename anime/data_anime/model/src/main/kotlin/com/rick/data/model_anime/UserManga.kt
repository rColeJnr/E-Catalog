package com.rick.data.model_anime

import com.rick.data.model_anime.model_jikan.Author
import com.rick.data.model_anime.model_jikan.Genre
import com.rick.data.model_anime.model_jikan.Published
import com.rick.data.model_anime.model_jikan.Serialization
import com.rick.data.model_anime.model_jikan.Theme

//@Serializable
data class UserManga internal constructor(
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
    val isFavorite: Boolean
) {
    constructor(manga: Manga, userData: MangaUserData) : this(
        id = manga.id,
        url = manga.url,
        images = manga.images,
        title = manga.title,
        titleEnglish = manga.titleEnglish,
        type = manga.type,
        chapters = manga.chapters,
        volumes = manga.volumes,
        publishing = manga.publishing,
        published = manga.published,
        score = manga.score,
        scoredBy = manga.scoredBy,
        rank = manga.rank,
        popularity = manga.popularity,
        favorites = manga.favorites,
        synopsis = manga.synopsis,
        background = manga.background,
        authors = manga.authors,
        serializations = manga.serializations,
        genres = manga.genres,
        themes = manga.themes,
        isFavorite = manga.id in userData.mangaFavoriteIds
    )
}

fun Manga.mapToUserManga(userData: MangaUserData): UserManga =
    UserManga(this, userData)