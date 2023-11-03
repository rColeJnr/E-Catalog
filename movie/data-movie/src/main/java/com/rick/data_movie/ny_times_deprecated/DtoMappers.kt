package com.rick.data_movie.ny_times_deprecated

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
        multimedia = multimedia.toMultimedia(),
        favorite = false
    )


fun MovieCatalogDto.toMovieCatalog(): MovieCatalog =
    MovieCatalog(
        movieCatalog = results.map { it.toMovie() },
        hasMore = has_more
    )

fun MultimediaDto.toMultimedia(): Multimedia =
    Multimedia(
        src = src
    )