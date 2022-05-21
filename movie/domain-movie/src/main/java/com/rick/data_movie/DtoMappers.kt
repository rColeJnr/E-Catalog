package com.rick.data_movie

fun LinkDto.toLink(): Link =
    Link(
        url = url
    )

fun ResultDto.toResult(): Result =
    Result(
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

fun MovieCatalogDto.toMovieCatalogEntity(): MovieCatalogEntitiy =
    MovieCatalogEntitiy(
        movieCatalog = results.map { it.toResult() },
        hasMore = has_more
    )

fun MovieCatalogEntitiy.toMovieCatalog(): MovieCatalog =
    MovieCatalog(
        results = movieCatalog,
        hasMore = hasMore
    )

fun MultimediaDto.toMultimedia(): Multimedia =
    Multimedia(
        src = src
    )