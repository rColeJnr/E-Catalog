package com.rick.ecatalogs.game.data_game.network

/**
 * IGDB queries (Apicalypse: https://api-docs.igdb.com/#apicalypse-1), sent as the POST body.
 * Plain strings, so they are easy to test.
 */
object IgdbQueries {

    const val PAGE_SIZE = 50
    const val ROW_SIZE = 20
    const val SEARCH_LIMIT = 25

    /** IGDB's own page visits; the other popularity types are "want to play", "playing"… */
    const val POPULARITY_TYPE_VISITS = 1

    private const val DAY_SECONDS = 24 * 60 * 60L

    /** Theme 42 is "Erotic": left out everywhere, like adult titles on the other catalogs. */
    private const val SAFE = "themes != (42)"

    /** Fields for lists (catalog, rows, search). */
    const val LIST_FIELDS = "fields name,summary,cover.image_id,genres.name,platforms.abbreviation," +
        "platforms.name,total_rating,total_rating_count,first_release_date;"

    /** Fields for the details screen. */
    const val DETAILS_FIELDS = "fields name,summary,storyline,cover.image_id,screenshots.image_id," +
        "videos.name,videos.video_id,genres.name,platforms.abbreviation,platforms.name," +
        "involved_companies.company.name,involved_companies.developer,involved_companies.publisher," +
        "total_rating,total_rating_count,first_release_date,url," +
        "similar_games.name,similar_games.cover.image_id,similar_games.total_rating," +
        "similar_games.first_release_date;"

    fun popularIds(page: Int): String =
        "fields game_id,value; where popularity_type = $POPULARITY_TYPE_VISITS; " +
            "sort value desc; limit $PAGE_SIZE; offset ${offset(page, PAGE_SIZE)};"

    fun gamesByIds(ids: List<Int>): String =
        "$LIST_FIELDS where id = (${ids.joinToString(",")}) & $SAFE; limit ${ids.size};"

    fun comingSoon(now: Long): String =
        "$LIST_FIELDS where first_release_date > $now & first_release_date < ${now + 180 * DAY_SECONDS} " +
            "& cover != null & $SAFE; sort hypes desc; limit $ROW_SIZE;"

    fun recentlyReleased(now: Long): String =
        "$LIST_FIELDS where first_release_date <= $now & first_release_date > ${now - 60 * DAY_SECONDS} " +
            "& cover != null & $SAFE; sort hypes desc; limit $ROW_SIZE;"

    const val GENRES = "fields name; sort name asc; limit 50;"

    fun byGenre(genreId: Int, page: Int): String =
        "$LIST_FIELDS where genres = ($genreId) & total_rating_count > 10 & $SAFE; " +
            "sort total_rating_count desc; limit $ROW_SIZE; offset ${offset(page, ROW_SIZE)};"

    fun details(id: Int): String = "$DETAILS_FIELDS where id = $id;"

    /** IGDB's search can't be combined with sort. Editions/bundles of a game are left out. */
    fun search(query: String): String =
        "search \"${query.replace("\\", "").replace("\"", "")}\"; $LIST_FIELDS " +
            "where version_parent = null & $SAFE; limit $SEARCH_LIMIT;"

    private fun offset(page: Int, size: Int) = (page.coerceAtLeast(1) - 1) * size
}
