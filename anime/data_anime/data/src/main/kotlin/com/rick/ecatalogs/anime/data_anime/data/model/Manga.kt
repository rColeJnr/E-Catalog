package com.rick.ecatalogs.anime.data_anime.data.model

import com.rick.ecatalogs.anime.data_anime.database.model.MangaEntity
import com.rick.ecatalogs.anime.data_anime.database.model.MangaRecentSearchQueryEntity
import com.rick.ecatalogs.anime.data_anime.model.MangaRecentSearchQuery
import com.rick.ecatalogs.anime.data_anime.model.model_jikan.Author
import com.rick.ecatalogs.anime.data_anime.model.model_jikan.Genre
import com.rick.ecatalogs.anime.data_anime.model.model_jikan.Published
import com.rick.ecatalogs.anime.data_anime.model.model_jikan.Serialization
import com.rick.ecatalogs.anime.data_anime.model.model_jikan.Theme
import com.rick.ecatalogs.anime.data_anime.network.model.MangaNetwork
import com.rick.ecatalogs.anime.data_anime.network.model.posterUrl

fun MangaNetwork.asEntity(): MangaEntity = MangaEntity(
    id = id,
    url = url ?: "",
    image = posterUrl ?: "",
    title = title ?: "",
    titleEnglish = titleEnglish ?: "",
    type = type ?: "",
    chapters = chapters ?: 0,
    volumes = volumes ?: 0,
    publishing = publishing ?: status.equals("Publishing", ignoreCase = true),
    published = Published(
        published?.string ?: listOfNotNull(startDate, endDate).joinToString(" to ")
    ),
    score = score ?: 0.0,
    scoredBy = scoredBy ?: 0,
    rank = rank ?: 0,
    popularity = popularity ?: 0,
    favorites = favorites ?: 0,
    synopsis = synopsis ?: "",
    background = background ?: "",
    authors = authors.orEmpty().mapNotNull { ref ->
        val id = ref.id ?: return@mapNotNull null
        Author(id = id, type = "people", name = ref.name.orEmpty(), url = ref.url.orEmpty())
    },
    serializations = serializations.orEmpty().mapNotNull { ref ->
        val id = ref.id ?: return@mapNotNull null
        Serialization(id = id, type = "manga", name = ref.name.orEmpty(), url = ref.url.orEmpty())
    },
    genres = genres.orEmpty().mapNotNull { it.name?.let(::Genre) },
    themes = themes.orEmpty().mapNotNull { it.name?.let(::Theme) },
)

internal val MangaEntity.isListEntryOnly: Boolean get() = popularity == 0

fun MangaRecentSearchQueryEntity.asExternalModel() = MangaRecentSearchQuery(
    query = query,
    queriedDate = queriedDate,
)
