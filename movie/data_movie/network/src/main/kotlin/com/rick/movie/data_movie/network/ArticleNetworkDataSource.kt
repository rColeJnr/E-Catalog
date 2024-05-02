package com.rick.movie.data_movie.network

import com.rick.movie.data_movie.network.model.ArticleResponse


interface ArticleNetworkDataSource {

    suspend fun fetchMovieArticles(
        page: Int,
        apikey: String,
        section_name: String = "section_name:\"Movies\" AND type_of_material:\"Review\"",
        sort: String = "newest",
    ): ArticleResponse

    suspend fun searchMovieArticles(
        apiKey: String,
        query: String,
        sort: String = "relevance",
        section_name: String = "section_name:\"Movies\" AND type_of_material:\"Review\"",
    ): ArticleResponse

}

//const val NY_TIMES_BASE_URL = "https://api.nytimes.com/"
//private const val QUERY_ORDER = "by-publication-date"
//private const val MOVIE_QUERY =
//private const val SORT =
