package com.rick.ecatalogs.anime.data_anime.data.model

import com.rick.ecatalogs.anime.data_anime.database.model.AnimeEntity
import com.rick.ecatalogs.anime.data_anime.database.model.AnimeRecentSearchQueryEntity
import com.rick.ecatalogs.anime.data_anime.model.AnimeRecentSearchQuery
import com.rick.ecatalogs.anime.data_anime.network.model.AnimeNetwork
import com.rick.ecatalogs.anime.data_anime.network.model.posterUrl

fun AnimeNetwork.asEntity(): AnimeEntity = AnimeEntity(
    id = id,
    url = url ?: "",
    images = posterUrl ?: "",
    trailer = trailer?.embedUrl ?: "",
    title = title ?: "",
    type = type ?: "",
    source = source ?: "",
    episodes = episodes ?: 0,
    airing = airing ?: status.equals("Currently Airing", ignoreCase = true),
    aired = aired?.string ?: listOfNotNull(startDate, endDate).joinToString(" to "),
    runtime = duration ?: "",
    rating = rating ?: "",
    score = score ?: 0.0,
    scoredBy = scoredBy ?: 0,
    rank = rank ?: 0,
    popularity = popularity ?: 0,
    favorites = favorites ?: 0,
    synopsis = synopsis ?: "",
    background = background ?: "",
    season = season ?: "",
    year = year ?: yearOf(aired?.from ?: startDate),
    genres = genres.orEmpty().mapNotNull { it.name }
)

internal fun AnimeNetwork.isAdult(): Boolean = rating?.trim()?.startsWith("Rx", ignoreCase = true) == true

internal val AnimeEntity.isListEntryOnly: Boolean get() = popularity == 0

internal fun yearOf(date: String?): Int =
    date?.let { Regex("\\d{4}").find(it)?.value?.toIntOrNull() } ?: 0

fun AnimeRecentSearchQueryEntity.asExternalModel() = AnimeRecentSearchQuery(
    query = query,
    queriedDate = queriedDate,
)
