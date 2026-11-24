package com.rick.ecatalogs.movie.data_movie.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** TMDB `person/{id}?append_to_response=combined_credits`. Optional fields default to null. */
@Serializable
data class PersonResponse(
    val id: Int,
    val name: String,
    val biography: String? = null,
    val birthday: String? = null,
    val deathday: String? = null,
    @SerialName("place_of_birth")
    @SerializedName("place_of_birth")
    val placeOfBirth: String? = null,
    @SerialName("known_for_department")
    @SerializedName("known_for_department")
    val knownForDepartment: String? = null,
    @SerialName("profile_path")
    @SerializedName("profile_path")
    val profilePath: String? = null,
    @SerialName("combined_credits")
    @SerializedName("combined_credits")
    val combinedCredits: CombinedCreditsResponse? = null,
)

@Serializable
data class CombinedCreditsResponse(
    val cast: List<PersonCreditResponse>? = null,
)

/** A movie (`title`, `release_date`) or series (`name`, `first_air_date`) the person acted in. */
@Serializable
data class PersonCreditResponse(
    val id: Int,
    @SerialName("media_type")
    @SerializedName("media_type")
    val mediaType: String? = null,
    val title: String? = null,
    val name: String? = null,
    val character: String? = null,
    @SerialName("poster_path")
    @SerializedName("poster_path")
    val posterPath: String? = null,
    @SerialName("release_date")
    @SerializedName("release_date")
    val releaseDate: String? = null,
    @SerialName("first_air_date")
    @SerializedName("first_air_date")
    val firstAirDate: String? = null,
    @SerialName("vote_count")
    @SerializedName("vote_count")
    val voteCount: Int? = null,
    @SerialName("vote_average")
    @SerializedName("vote_average")
    val voteAverage: Double? = null,
)
