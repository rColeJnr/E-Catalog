package com.rick.data_movie.imdb.movie_model

data class IMDBMovie(
    val id: String,
    val title: String,
    val type: String,
    val year: String,
    val image: String,
    val releaseDate: String,
    val runtimeStr: String,
    val plot: String,
    val awards: String,
    val actorList: List<Actor>,
    val genres: String,
    val directorList: List<Director>,
    val companies: String,
    val countries: String,
    val languages: String,
    val contentRating: String,
    val ratings: Ratings,
    val images: Images,
    val boxOffice: BoxOffice,
    val similars: List<Similar>,
    val errorMessage: String?
)