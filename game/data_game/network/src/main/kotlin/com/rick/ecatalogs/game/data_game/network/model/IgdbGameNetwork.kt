package com.rick.ecatalogs.game.data_game.network.model

import com.google.gson.annotations.SerializedName

/**
 * A game from IGDB's `games` endpoint. Every field is optional: IGDB leaves out what a game
 * doesn't have, and only returns the fields a query asks for.
 */
data class IgdbGameNetwork(
    val id: Int,
    val name: String? = null,
    val summary: String? = null,
    val storyline: String? = null,
    val cover: IgdbImageNetwork? = null,
    val screenshots: List<IgdbImageNetwork>? = null,
    val videos: List<IgdbVideoNetwork>? = null,
    val genres: List<IgdbNamedNetwork>? = null,
    val platforms: List<IgdbPlatformNetwork>? = null,
    @SerializedName("involved_companies")
    val involvedCompanies: List<IgdbInvolvedCompanyNetwork>? = null,
    @SerializedName("total_rating")
    val totalRating: Double? = null,
    @SerializedName("total_rating_count")
    val totalRatingCount: Int? = null,
    /** Epoch seconds. */
    @SerializedName("first_release_date")
    val firstReleaseDate: Long? = null,
    @SerializedName("similar_games")
    val similarGames: List<IgdbGameNetwork>? = null,
    val url: String? = null,
)

data class IgdbImageNetwork(
    @SerializedName("image_id")
    val imageId: String? = null,
)

data class IgdbVideoNetwork(
    val name: String? = null,
    /** YouTube id */
    @SerializedName("video_id")
    val videoId: String? = null,
)

data class IgdbNamedNetwork(
    val id: Int,
    val name: String? = null,
)

data class IgdbPlatformNetwork(
    val id: Int,
    val name: String? = null,
    val abbreviation: String? = null,
)

data class IgdbInvolvedCompanyNetwork(
    val company: IgdbNamedNetwork? = null,
    val developer: Boolean? = null,
    val publisher: Boolean? = null,
)

data class IgdbPopularityNetwork(
    @SerializedName("game_id")
    val gameId: Int,
    val value: Double? = null,
)

data class TwitchTokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    /** Seconds */
    @SerializedName("expires_in")
    val expiresIn: Long,
)
