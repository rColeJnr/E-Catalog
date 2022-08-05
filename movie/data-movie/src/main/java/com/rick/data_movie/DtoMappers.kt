package com.rick.data_movie

fun LinkDto.toLink(): Link =
    Link(
        url = url
    )

fun ResultDto.toMovie(): Movie =
    Movie(
        title = display_title,
        summary = summary_short,
        openingDate = opening_date,
        rating = mpaa_rating,
        link = link.toLink(),
        multimedia = multimedia.toMultimedia()
    )

fun ResultDto.toMovieEntity(): MovieEntity =
    MovieEntity(
        id = id,
        title = display_title,
        summary = summary_short,
        openingDate = opening_date,
        rating = mpaa_rating,
        link = link.toLink(),
        multimedia = multimedia.toMultimedia()
    )

fun MovieEntity.toMovie(): Movie =
    Movie(
        title = title,
        summary = summary,
        openingDate = openingDate,
        rating = rating,
        link = link,
        multimedia = multimedia
    )

fun MovieCatalogDto.toMovieCatalog(): MovieCatalog =
    MovieCatalog(
        movieCatalog = results.map { it.toMovieEntity() },
        hasMore = has_more
    )

fun MultimediaDto.toMultimedia(): Multimedia =
    Multimedia(
        src = src
    )