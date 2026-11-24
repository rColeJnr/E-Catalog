package com.rick.ecatalogs.anime.data_anime.model.browse

/** A Jikan (MyAnimeList) genre. Names are English only. */
data class JikanGenre(val id: Int, val name: String)

/** An anime or manga in a row ("This season", "If you liked this"). */
data class JikanItem(val id: Int, val title: String, val imageUrl: String?)

/** Which anime list the catalog shows. */
enum class AnimeList { POPULAR, TOP_RATED, AIRING, UPCOMING, FAVORITES }

/** Which manga list the catalog shows. */
enum class MangaList { POPULAR, TOP_RATED, PUBLISHING, FAVORITES }

/** Anime details beyond the list data: trailer, where to stream it, when it airs. */
data class AnimeExtras(
    /** YouTube video id of the trailer, if any. */
    val trailerYoutubeId: String?,
    val streaming: List<StreamingService>,
    /** Only for currently airing anime. */
    val broadcast: Broadcast?,
)

data class StreamingService(val name: String, val url: String)

/** Weekly broadcast slot in Japan time, e.g. day "Saturdays", time "23:30", zone "Asia/Tokyo". */
data class Broadcast(val day: String, val time: String, val timezone: String)

data class AnimeEpisode(
    val number: Int,
    val title: String,
    /** yyyy-MM-dd, or null when unknown. */
    val airDate: String?,
    val score: Double?,
    val filler: Boolean,
    val recap: Boolean,
)
