package com.rick.data.model_anime.model_jikan.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Aired(
    @SerialName("from")
    val from: String,
    @SerialName("prop")
    val prop: Prop,
    @SerialName("string")
    val string: String,
    @SerialName("to")
    val to: String?
) {
    @Serializable
    data class Prop(
        @SerialName("from")
        val from: From,
        @SerialName("to")
        val to: To
    ) {
        @Serializable
        data class From(
            @SerialName("day")
            val day: Int,
            @SerialName("month")
            val month: Int,
            @SerialName("year")
            val year: Int
        )

        @Serializable
        data class To(
            @SerialName("day")
            val day: Int?,
            @SerialName("month")
            val month: Int?,
            @SerialName("year")
            val year: Int?
        )
    }
}

@Serializable
data class Broadcast(
    @SerialName("day")
    val day: String?,
    @SerialName("string")
    val string: String?,
    @SerialName("time")
    val time: String?,
    @SerialName("timezone")
    val timezone: String?
)

@Serializable
data class Demographic(
    @SerialName("mal_id")
    val malId: Int,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: String,
    @SerialName("url")
    val url: String
)

@Serializable
data class Genre(
    @SerialName("mal_id")
    val malId: Int,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: String,
    @SerialName("url")
    val url: String
)

@Serializable
data class Images(
    @SerialName("jpg")
    val jpg: Jpg,
    @SerialName("webp")
    val webp: Webp
) {
    @Serializable
    data class Jpg(
        @SerialName("image_url")
        val imageUrl: String,
        @SerialName("large_image_url")
        val largeImageUrl: String,
        @SerialName("small_image_url")
        val smallImageUrl: String
    )

    @Serializable
    data class Webp(
        @SerialName("image_url")
        val imageUrl: String,
        @SerialName("large_image_url")
        val largeImageUrl: String,
        @SerialName("small_image_url")
        val smallImageUrl: String
    )
}

@Serializable
data class Licensor(
    @SerialName("mal_id")
    val malId: Int,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: String,
    @SerialName("url")
    val url: String
)

@Serializable
data class Producer(
    @SerialName("mal_id")
    val malId: Int,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: String,
    @SerialName("url")
    val url: String
)

@Serializable
data class Studio(
    @SerialName("mal_id")
    val malId: Int,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: String,
    @SerialName("url")
    val url: String
)

@Serializable
data class Theme(
    @SerialName("mal_id")
    val malId: Int,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: String,
    @SerialName("url")
    val url: String
)

@Serializable
data class Title(
    @SerialName("title")
    val title: String,
    @SerialName("type")
    val type: String
)

@Serializable
data class Trailer(
    @SerialName("embed_url")
    val embedUrl: String?,
    @SerialName("images")
    val images: Images,
    @SerialName("url")
    val url: String?,
    @SerialName("youtube_id")
    val youtubeId: String?
) {
    @Serializable
    data class Images(
        @SerialName("image_url")
        val imageUrl: String?,
        @SerialName("large_image_url")
        val largeImageUrl: String?,
        @SerialName("maximum_image_url")
        val maximumImageUrl: String?,
        @SerialName("medium_image_url")
        val mediumImageUrl: String?,
        @SerialName("small_image_url")
        val smallImageUrl: String?
    )
}

@Serializable
data class Pagination(
    @SerialName("current_page")
    val currentPage: Int,
    @SerialName("has_next_page")
    val hasNextPage: Boolean,
    @SerialName("items")
    val items: Items,
    @SerialName("last_visible_page")
    val lastVisiblePage: Int
) {
    @Serializable
    data class Items(
        @SerialName("count")
        val count: Int,
        @SerialName("per_page")
        val perPage: Int,
        @SerialName("total")
        val total: Int
    )
}