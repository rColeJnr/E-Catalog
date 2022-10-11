package com.rick.data_book.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class TranslatorDto(
    @SerializedName("birth_year")
    val birthYear: Int?,
    @SerializedName("death_year")
    val deathYear: Int?,
    @SerializedName("name")
    val name: String
)

@Parcelize
data class Translator(
    @SerializedName("name")
    val name: String
): Parcelable