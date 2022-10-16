package com.rick.data_anime.model_jikan

fun JikanResponseDto.toJikanResponse(): JikanResponse =
    JikanResponse(
        pagination,
        data.map { it.toAnime() }
    )

fun JikanDto.toAnime(): Jikan = Jikan(
    malId,
    url,
    images.toImages(),
    trailer.toTrailers(),
    title = title,
    type,
    source,
    episodes,
    chapters,
    volumes,
    status,
    aired.toAired(),
    publishedDto.toPublished(),
    duration,
    rating,
    score,
    scoredBy,
    rank,
    popularity,
    members,
    favorites,
    synopsis,
    background,
    genres.map { it.toGenre() },
    authors,
    serializations,
    themes
)

fun ImagesDto.toImages() = Images(
    jpg
)

fun TrailerDto.toTrailers() = Trailer(
    url
)

fun AiredDto.toAired() = Aired(
    prop, string
)

fun PublishedDto.toPublished() = Published(
    prop, string
)

fun GenreDto.toGenre() = Genre(
    name
)