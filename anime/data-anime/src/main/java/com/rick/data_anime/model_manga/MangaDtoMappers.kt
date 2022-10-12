package com.rick.data_anime.model_manga

fun MangaResponseDto.toMangaResponse() = MangaResponse(
    manga.map { it.toManga() }, pagination
)

fun MangaDto.toManga() = Manga(
    malId,
    url,
    images.toImages(),
    approved,
    title,
    titleJapanese,
    type,
    chapters,
    volumes,
    status,
    publishedDto.toPublished(),
    score,
    scoredBy,
    rank,
    popularity,
    members,
    favorites,
    synopsis,
    background,
    authors,
    serializations,
    genres.map { it.toGenre() },
    themes.map { it.toTheme() }
)

fun ImagesDto.toImages() = Images(
    jpg
)

fun PublishedDto.toPublished() = Published(
    prop, string
)

fun GenreDto.toGenre() = Genre(
    name
)

fun ThemeDto.toTheme() = Theme(
    name
)