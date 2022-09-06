package com.rick.data_movie.imdb.movie_model


import com.google.gson.annotations.SerializedName

data class LanguageDto(
    @SerializedName("key")
    val key: String,
    @SerializedName("value")
    val value: String
)

//@Parcelize
//data class Language(
//    @SerializedName("key")
//    val key: String,
//    @SerializedName("value")
//    val value: String
//) : Parcelable