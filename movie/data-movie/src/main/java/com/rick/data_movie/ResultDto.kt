package com.rick.data_movie

data class ResultDto(
    val id: Long,
    val byline: String,
    val critics_pick: Int,
    val date_updated: String,
    val display_title: String,
    val headline: String,
    val link: LinkDto,
    val mpaa_rating: String,
    val multimedia: MultimediaDto,
    val opening_date: String,
    val publication_date: String,
    val summary_short: String
)