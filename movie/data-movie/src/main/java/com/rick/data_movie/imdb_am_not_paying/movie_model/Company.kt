package com.rick.data_movie.imdb_am_not_paying.movie_model


import com.google.gson.annotations.SerializedName

data class CompanyDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)
//
//@Parcelize
//data class Company(
//    @SerializedName("id")
//    val id: String,
//    @SerializedName("name")
//    val name: String
//) : Parcelable