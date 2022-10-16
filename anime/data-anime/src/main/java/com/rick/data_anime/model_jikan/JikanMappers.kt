package com.rick.data_anime.model_jikan

fun JikanResponseDto.toJikanResponse(): JikanResponse =
    JikanResponse(
        pagination,
        data.map { it.toJikan() }
    )

fun JikanDto.toJikan(): Jikan =
    Jikan(
        malId = malId,
        url = url,
        images = images.toImages(),
        trailer = trailer?.toTrailers(),
        title = title,
        type = type,
        source = source,
        episodes = episodes,
        chapters = chapters,
        volumes = volumes,
        status = status,
        airing = airing,
        aired = aired?.toAired(),
        published = publishedDto?.toPublished(),
        duration = duration,
        rating = rating,
        score = score,
        scoredBy = scoredBy,
        rank = rank,
        popularity = popularity,
        members = members,
        favorites = favorites,
        synopsis = synopsis,
        background = background,
        genres = genres?.map { it.toGenre() },
        authors = authors,
        serializations = serializations,
        themes = themes
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