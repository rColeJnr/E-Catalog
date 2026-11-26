package com.rick.ecatalogs.book.data_book.model.gutenberg

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@Parcelize
@Serializable
data class Author(
    // Gutendex is parsed with Gson (@SerializedName), the demo assets with kotlinx (@SerialName)
    @SerializedName("birth_year")
    @SerialName("birth_year")
    val birthYear: Int?,
    @SerializedName("death_year")
    @SerialName("death_year")
    val deathYear: Int?,
    val name: String
) /*: Parcelable {
    override fun toString(): String {
        return name
    }
}*/