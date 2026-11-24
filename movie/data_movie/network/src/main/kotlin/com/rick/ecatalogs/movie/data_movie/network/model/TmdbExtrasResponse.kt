package com.rick.ecatalogs.movie.data_movie.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Extra sections TMDB returns inside the details response when they are named in
// append_to_response (credits, videos, watch/providers, release_dates / content_ratings).
// Every field is nullable with a default: Gson leaves missing fields null, and the demo JSON
// assets don't contain these sections at all.

@Serializable
data class CreditsResponse(
    val cast: List<CastResponse>? = null,
)

@Serializable
data class CastResponse(
    val id: Int,
    val name: String,
    val character: String? = null,
    @SerialName("profile_path")
    @SerializedName("profile_path")
    val profilePath: String? = null,
    val order: Int? = null,
)

@Serializable
data class VideosResponse(
    val results: List<VideoResponse>? = null,
)

@Serializable
data class VideoResponse(
    val key: String,
    val name: String? = null,
    val site: String? = null,
    val type: String? = null,
    val official: Boolean? = null,
)

/** `results` is keyed by country code (ISO 3166-1), e.g. "US", "BR". */
@Serializable
data class WatchProvidersResponse(
    val results: Map<String, WatchProviderCountryResponse>? = null,
)

@Serializable
data class WatchProviderCountryResponse(
    val link: String? = null,
    val flatrate: List<WatchProviderResponse>? = null,
    val rent: List<WatchProviderResponse>? = null,
    val buy: List<WatchProviderResponse>? = null,
)

@Serializable
data class WatchProviderResponse(
    @SerialName("provider_id")
    @SerializedName("provider_id")
    val providerId: Int,
    @SerialName("provider_name")
    @SerializedName("provider_name")
    val providerName: String,
    @SerialName("logo_path")
    @SerializedName("logo_path")
    val logoPath: String? = null,
    @SerialName("display_priority")
    @SerializedName("display_priority")
    val displayPriority: Int? = null,
)

/** Movies: certifications per country. */
@Serializable
data class ReleaseDatesResponse(
    val results: List<ReleaseDatesCountryResponse>? = null,
)

@Serializable
data class ReleaseDatesCountryResponse(
    @SerialName("iso_3166_1")
    @SerializedName("iso_3166_1")
    val country: String,
    @SerialName("release_dates")
    @SerializedName("release_dates")
    val releaseDates: List<ReleaseDateResponse>? = null,
)

@Serializable
data class ReleaseDateResponse(
    val certification: String? = null,
    /** 1 premiere, 2 limited theatrical, 3 theatrical, 4 digital, 5 physical, 6 TV. */
    val type: Int? = null,
)

/** Series: content ratings per country. */
@Serializable
data class ContentRatingsResponse(
    val results: List<ContentRatingResponse>? = null,
)

@Serializable
data class ContentRatingResponse(
    @SerialName("iso_3166_1")
    @SerializedName("iso_3166_1")
    val country: String,
    val rating: String? = null,
)
