package com.rick.ecatalogs.anime.data_anime.model.browse

/** A Jikan (MyAnimeList) genre. Names are English only. */
data class JikanGenre(val id: Int, val name: String)

/** An anime or manga in a row ("This season", "If you liked this"). */
data class JikanItem(
    val id: Int,
    val title: String,
    val imageUrl: String?,
    val date: String? = null,
    val score: Double? = null,
)

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
    /** Adaptations (e.g. the source manga), sequels, prequels… */
    val relations: List<JikanRelation> = emptyList(),
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

/** A related title: [relation] is Jikan's label ("Adaptation", "Sequel"…). */
data class JikanRelation(val relation: String, val id: Int, val isAnime: Boolean, val name: String)

data class AnimeCharacter(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val isMain: Boolean,
    /** The Japanese voice actor, if listed. */
    val voiceActor: VoiceActor?,
)

data class VoiceActor(val id: Int, val name: String, val imageUrl: String?)

/** A voice actor's page. */
data class JikanPerson(
    val id: Int,
    val name: String,
    /** Name in Japanese order ("family given"), when known. */
    val nativeName: String?,
    /** yyyy-MM-dd, or null. */
    val birthday: String?,
    val about: String,
    val imageUrl: String?,
    val roles: List<VoiceRole>,
)

data class VoiceRole(
    val animeId: Int,
    val animeTitle: String,
    val animeImageUrl: String?,
    val characterName: String,
    val isMain: Boolean,
)

/** An anime in the weekly schedule. */
data class ScheduledAnime(
    val id: Int,
    val title: String,
    val imageUrl: String?,
    val broadcast: Broadcast?,
    val score: Double?,
)
