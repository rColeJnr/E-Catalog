package com.rick.ecatalogs.anime.data_anime.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EdgeMeta(
    val cached: Boolean? = null,
    val stale: Boolean? = null,
    val refreshFailed: Boolean? = null,
    val fetchedAt: String? = null,
    val pagination: EdgePagination? = null,
)

val EdgeMeta?.isStale: Boolean get() = this?.stale == true

@Serializable
data class EdgePagination(
    val page: Int? = null,
    val limit: Int? = null,
    val count: Int? = null,
    val total: Int? = null,
    val hasNextPage: Boolean? = null,
)

val EdgeMeta?.hasNextPage: Boolean get() = this?.pagination?.hasNextPage == true

@Serializable
data class EdgeErrorResponse(val error: EdgeError? = null)

@Serializable
data class EdgeError(
    val code: String? = null,
    val message: String? = null,
    val requestId: String? = null,
)

@Serializable
data class EdgeRef(
    @SerialName("malId") @SerializedName("malId") val id: Int? = null,
    val name: String? = null,
    val url: String? = null,
)

@Serializable
data class EdgeImages(
    val small: String? = null,
    val medium: String? = null,
    val large: String? = null,
)

@Serializable
data class EdgeDateRange(
    val from: String? = null,
    val to: String? = null,
    val string: String? = null,
)
