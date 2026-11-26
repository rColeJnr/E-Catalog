package com.rick.ecatalogs.game.data_game.data.model

import com.rick.ecatalogs.game.data_game.model.Game
import com.rick.ecatalogs.game.data_game.model.GameDetails
import com.rick.ecatalogs.game.data_game.model.GameGenre
import com.rick.ecatalogs.game.data_game.network.model.IgdbGameNetwork
import com.rick.ecatalogs.game.data_game.network.model.IgdbNamedNetwork

internal const val MAX_SCREENSHOTS = 10
internal const val MAX_SIMILAR = 10

fun IgdbGameNetwork.asGame() = Game(
    id = id,
    name = name.orEmpty(),
    coverImageId = cover?.imageId?.takeIf { it.isNotBlank() },
    summary = summary.orEmpty().trim(),
    rating = totalRating,
    ratingCount = totalRatingCount ?: 0,
    releaseDate = firstReleaseDate,
    genres = genres.orEmpty().mapNotNull { it.name?.takeIf(String::isNotBlank) },
    platforms = platforms.orEmpty().mapNotNull { platform ->
        (platform.abbreviation ?: platform.name)?.takeIf(String::isNotBlank)
    },
)

fun IgdbGameNetwork.asGameDetails() = GameDetails(
    game = asGame(),
    storyline = storyline.orEmpty().trim(),
    screenshotIds = screenshots.orEmpty().mapNotNull { it.imageId?.takeIf(String::isNotBlank) }
        .take(MAX_SCREENSHOTS),
    // Prefer a video called "trailer"; otherwise the first one
    trailerYoutubeId = videos.orEmpty()
        .sortedByDescending { it.name.orEmpty().contains("trailer", ignoreCase = true) }
        .firstNotNullOfOrNull { it.videoId?.takeIf(String::isNotBlank) },
    developers = involvedCompanies.orEmpty().filter { it.developer == true }
        .mapNotNull { it.company?.name }.distinct(),
    publishers = involvedCompanies.orEmpty().filter { it.publisher == true }
        .mapNotNull { it.company?.name }.distinct(),
    similar = similarGames.orEmpty().filter { !it.name.isNullOrBlank() }.map { it.asGame() }.take(MAX_SIMILAR),
    igdbUrl = url,
)

fun IgdbNamedNetwork.asGenre(): GameGenre? = name?.takeIf(String::isNotBlank)?.let { GameGenre(id, it) }
