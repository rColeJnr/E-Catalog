package com.rick.data_movie.imdb.movie_model

fun ActorDto.toActor(): Actor = Actor(
    id,
    image,
    name,
    asCharacter
)

fun RatingsDto.toRatings(): Ratings = Ratings(
    imDb,
    metacritic,
    theMovieDb,
    rottenTomatoes,
    errorMessage
)

fun ItemDto.toItem(): Item = Item(
    image
)

fun ImageDto.toImage(): Image = Image(
    items = items.map { it.toItem() },
    errorMessage
)

fun BoxOfficeDto.toBoxOffice(): BoxOffice = BoxOffice(
    budget,
    openingWeekendUSA,
    cumulativeWorldwideGross
)

fun SimilarDto.toSimilars(): Similar = Similar(
    id,
    title,
    image,
    imDbRating
)

fun TvSeriesInfoDto.toTvSeriesInfo(): TvSeriesInfo = TvSeriesInfo(
    yearEnd,
    creators,
    seasons
)

fun IMDBMovieDto.toImdbMovie(): IMDBMovie = IMDBMovie(
    id,
    title,
    type,
    year,
    image,
    releaseDate,
    runtimeStr,
    plot,
    awards,
    directors,
    writers,
    actorList.map { it.toActor() },
    genres,
    companies,
    countries,
    languages,
    ratings.toRatings(),
    images.toImage(),
    boxOffice.toBoxOffice(),
    similars.map { it.toSimilars() },
    tvSeriesInfo?.toTvSeriesInfo(),
    errorMessage,
    favorite = false
)