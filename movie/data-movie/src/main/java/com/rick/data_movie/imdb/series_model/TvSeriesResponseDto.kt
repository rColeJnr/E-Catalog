package com.rick.data_movie.imdb.series_model


import com.google.gson.annotations.SerializedName

data class TvSeriesResponseDto(
    @SerializedName("items")
    val tvSeries: List<TvSeriesDto>,
    @SerializedName("errorMessage")
    val errorMessage: String
) {
    fun toTvSeriesResponse() : TvSeriesResponse =
        TvSeriesResponse(
            tvSeries = tvSeries.map { it.toTvSeries() },
            errorMessage = errorMessage
        )
}

data class TvSeriesResponse(
    @SerializedName("items")
    val tvSeries: List<TvSeries>,
    @SerializedName("errorMessage")
    val errorMessage: String
)