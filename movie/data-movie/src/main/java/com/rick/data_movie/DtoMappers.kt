package com.rick.data_movie

fun LinkDto.toLink(): Link =
    Link(
        url = url
    )

fun ResultDto.toResult(): Movie =
    Movie(
        title = display_title,
        summary = summary_short,
        openingDate = opening_date,
        rating = mpaa_rating,
        link = link.toLink(),
        multimedia = multimedia.toMultimedia()
    )

fun MovieCatalogDto.toMovieCatalog(): MovieCatalog =
    MovieCatalog(
        results = results.map { it.toResult() },
        hasMore = has_more
    )

fun MovieCatalogDto.toMovieCatalogEntity(): MovieCatalog =
    MovieCatalog(
        movieCatalog = results.map { it.toResult() },
        hasMore = has_more
    )

fun MovieCatalog.toMovieCatalog(): MovieCatalog =
    MovieCatalog(
        results = movieCatalog,
        hasMore = hasMore
    )

fun MultimediaDto.toMultimedia(): Multimedia =
    Multimedia(
        src = src
    )