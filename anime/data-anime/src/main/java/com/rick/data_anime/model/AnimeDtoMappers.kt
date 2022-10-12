package com.rick.data_anime.model

fun AnimeResponseDto.toAnimeResponse(): AnimeResponse =
    AnimeResponse(
        pagination,
        animes.map { it.toAnime() }
    )

fun AnimeDto.toAnime(): Anime= Anime(
    malId,
    url,
    images.toImages(),
    trailer.toTrailers(),
    titleEnglish,
    titleJapanese,
    type,
    source,
    episodes,
    status,
    aired.toAired(),
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
    genres.map { it.toGenre() }
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

fun GenreDto.toGenre() = Genre(
    name
)