package com.rick.moviecatalog.data

import com.rick.moviecatalog.data.model.Link
import com.rick.moviecatalog.data.model.MovieCatalog
import com.rick.moviecatalog.data.model.Multimedia
import com.rick.moviecatalog.data.model.Result
import com.rick.moviecatalog.data.remote.LinkDto
import com.rick.moviecatalog.data.remote.MovieCatalogDto
import com.rick.moviecatalog.data.remote.MultimediaDto
import com.rick.moviecatalog.data.remote.ResultDto

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

fun MultimediaDto.toMultimedia(): Multimedia =
    Multimedia(
        src = src
    )