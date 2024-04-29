package com.rick.data.model_book.gutenberg

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@Parcelize
@Serializable
data class Author(
    @SerialName("birth_year")
    val birthYear: Int?,
    @SerialName("death_year")
    val deathYear: Int?,
    val name: String
) /*: Parcelable {
    override fun toString(): String {
        return name
    }
}*/