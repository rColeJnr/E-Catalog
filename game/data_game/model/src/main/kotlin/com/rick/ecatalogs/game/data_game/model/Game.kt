package com.rick.ecatalogs.game.data_game.model

/** A game as listed in the catalog, search results and favorites (IGDB). */
data class Game(
    val id: Int,
    val name: String,
    /** IGDB image id of the cover; see [igdbImageUrl]. */
    val coverImageId: String?,
    val summary: String,
    /** Critic + user rating, 0–100. */
    val rating: Double?,
    val ratingCount: Int,
    /** First release, epoch seconds (UTC). */
    val releaseDate: Long?,
    val genres: List<String>,
    /** Platform abbreviations (PS5, PC, Switch…). */
    val platforms: List<String>,
)

/** Everything the details screen shows. Not stored: fetched when the screen opens. */
data class GameDetails(
    val game: Game,
    val storyline: String,
    val screenshotIds: List<String>,
    /** YouTube video id of the first trailer, if IGDB has one. */
    val trailerYoutubeId: String?,
    val developers: List<String>,
    val publishers: List<String>,
    val similar: List<Game>,
    /** The game's page on igdb.com. */
    val igdbUrl: String?,
)

data class GameGenre(val id: Int, val name: String)

/** The ids of the user's favorite games (DataStore). */
data class GameUserData(val gameFavoriteIds: Set<Int>)

data class UserGame(val game: Game, val isFavorite: Boolean)

data class UserGameDetails(val details: GameDetails, val isFavorite: Boolean)

fun Game.asUserGame(userData: GameUserData) = UserGame(this, id in userData.gameFavoriteIds)

fun List<Game>.asUserGames(userData: GameUserData) = map { it.asUserGame(userData) }
