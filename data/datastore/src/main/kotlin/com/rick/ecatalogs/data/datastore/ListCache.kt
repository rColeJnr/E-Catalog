package com.rick.ecatalogs.data.datastore

import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Remembers when each catalog list was last downloaded, so opening a screen reuses the list
 * stored on the phone instead of downloading it again. A list counts as fresh for as long as
 * the background sync interval chosen in Settings (a week by default): the content behind
 * these lists changes about that often.
 *
 * Paging's RemoteMediator.initialize() asks [isFresh] to decide between showing the stored
 * list and refreshing it; a manual retry (adapter.refresh()) always downloads.
 */
class ListCache @Inject constructor(
    private val preferences: PreferencesDataSource,
) {
    suspend fun isFresh(list: String, now: Long = System.currentTimeMillis()): Boolean {
        val refreshedAt = preferences.listRefreshedAt(list)
        if (refreshedAt <= 0L) return false
        val intervalHours = preferences.settingsUserData.first().syncIntervalHours
        return now - refreshedAt in 0 until TimeUnit.HOURS.toMillis(intervalHours.toLong())
    }

    /** For lists that change daily (e.g. series airing today): fresh only on the same local day. */
    suspend fun isFreshToday(list: String, now: Long = System.currentTimeMillis()): Boolean {
        val refreshedAt = preferences.listRefreshedAt(list)
        if (refreshedAt <= 0L || refreshedAt > now) return false
        val zone = ZoneId.systemDefault()
        return Instant.ofEpochMilli(refreshedAt).atZone(zone).toLocalDate() ==
            Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
    }

    suspend fun markRefreshed(list: String, now: Long = System.currentTimeMillis()) =
        preferences.setListRefreshedAt(list, now)

    /**
     * For lists stored in one table under several filters (only the last one is on the phone):
     * marks [list] fresh and every other list starting with [prefix] as not downloaded.
     */
    suspend fun markRefreshedExclusive(
        list: String,
        prefix: String,
        now: Long = System.currentTimeMillis(),
    ) = preferences.replaceListRefreshedAt(prefix, list, now)
}

/** Keys for [ListCache]. */
object CachedList {
    const val TRENDING_MOVIES = "trending_movies"
    const val TRENDING_SERIES = "trending_series"
    const val ARTICLES = "articles"
    const val ANIME = "anime"
    const val MANGA = "manga"
    /** Prefix of the Gutenberg list keys (one per filter, see GutenbergFilter.cacheKey). */
    const val GUTENBERG = "gutenberg"
    const val UPCOMING_MOVIES = "upcoming_movies"
    const val NOW_PLAYING_MOVIES = "now_playing_movies"
    const val AIRING_TODAY_SERIES = "airing_today_series"
    const val ANIME_SEASON_NOW = "anime_season_now"
    const val ANIME_SEASON_UPCOMING = "anime_season_upcoming"
    const val GAMES = "games"
    const val GAMES_COMING_SOON = "games_coming_soon"
    const val GAMES_RECENTLY_RELEASED = "games_recently_released"
    const val GAME_GENRES = "game_genres"

    /** A featured manga row on the manga screen, per list (e.g. "PUBLISHING"). */
    fun jikanMangaRow(list: String) = "jikan_manga_row:$list"

    /** Jikan genre lists, per type ("anime" / "manga"). */
    fun jikanGenres(type: String) = "jikan_genres:$type"

    /** TMDB genre names, per type ("movie" / "tv") and language. */
    fun tmdbGenres(type: String, language: String) = "tmdb_genres:$type:$language"

    /** One NYT list, by its list name (e.g. "business-books"). */
    fun bestseller(listName: String) = "bestseller:$listName"
}
