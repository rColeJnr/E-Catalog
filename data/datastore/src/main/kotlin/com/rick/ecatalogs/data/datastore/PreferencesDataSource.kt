package com.rick.ecatalogs.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.rick.ecatalogs.anime.data_anime.model.AnimeUserData
import com.rick.ecatalogs.anime.data_anime.model.MangaUserData
import com.rick.ecatalogs.game.data_game.model.GameUserData
import com.rick.ecatalogs.book.data_book.model.bestseller.BestsellerUserData
import com.rick.ecatalogs.book.data_book.model.gutenberg.GutenbergUserData
import com.rick.ecatalogs.movie.data_movie.model.ArticleUserData
import com.rick.ecatalogs.movie.data_movie.model.TrendingMovieUserData
import com.rick.ecatalogs.movie.data_movie.model.TrendingSeriesUserData
import com.rick.ecatalogs.settings.data_settings.model.DEFAULT_SYNC_INTERVAL_HOURS
import com.rick.ecatalogs.settings.data_settings.model.SYNC_INTERVAL_OPTIONS_HOURS
import com.rick.ecatalogs.settings.data_settings.model.NotificationCategory
import com.rick.ecatalogs.settings.data_settings.model.SettingsUserData
import com.rick.ecatalogs.settings.data_settings.model.ThemeConfig
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class PreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    //
    val settingsUserData = userPreferences.data
        .map {
            SettingsUserData(
                userName = it.userName,
                themeConfig = when (it.theme) {
                    THEME_LIGHT -> ThemeConfig.LIGHT
                    THEME_FOLLOW_SYSTEM -> ThemeConfig.FOLLOW_SYSTEM
                    // Unset (0): the app is dark-first
                    else -> ThemeConfig.DARK
                },
                syncEnabled = !it.syncDisabled,
                // Unset (0) or anything that isn't an option: the weekly default
                syncIntervalHours = it.syncIntervalHours
                    .takeIf { hours -> hours in SYNC_INTERVAL_OPTIONS_HOURS }
                    ?: DEFAULT_SYNC_INTERVAL_HOURS,
                enabledNotifications = buildSet {
                    if (!it.notificationsDisabledMovies) add(NotificationCategory.TRENDING_MOVIES)
                    if (!it.notificationsDisabledSeries) add(NotificationCategory.TRENDING_SERIES)
                    if (!it.notificationsDisabledArticles) add(NotificationCategory.ARTICLES)
                    if (!it.notificationsDisabledBestsellers) add(NotificationCategory.BESTSELLERS)
                    if (!it.notificationsDisabledGames) add(NotificationCategory.GAMES)
                },
            )
        }
        .distinctUntilChanged()

    val animeUserData = userPreferences.data
        .map {
            AnimeUserData(
                animeFavoriteIds = it.animeFavoriteIdsMap.keys
            )
        }
        .distinctUntilChanged()

    val mangaUserData = userPreferences.data
        .map {
            MangaUserData(
                mangaFavoriteIds = it.mangaFavoriteIdsMap.keys
            )
        }
        .distinctUntilChanged()

    val bestsellerUserData = userPreferences.data
        .map {
            BestsellerUserData(
                bestsellerFavoriteIds = it.bestsellerFavoriteIdsMap.keys,
            )
        }
        .distinctUntilChanged()

    val gutenbergUserData = userPreferences.data
        .map {
            GutenbergUserData(
                gutenbergFavoriteIds = it.gutenbergFavoriteIdsMap.keys,
            )
        }
        .distinctUntilChanged()

    val articleUserData = userPreferences.data
        .map {
            ArticleUserData(
                nyTimesArticlesFavoriteIds = it.nyTimesArticlesFavoriteIdsMap.keys,
            )
        }
        .distinctUntilChanged()

    val trendingMovieUserData = userPreferences.data
        .map {
            TrendingMovieUserData(
                trendingMovieFavoriteIds = it.trendingMovieFavoriteIdsMap.keys,
            )
        }
        .distinctUntilChanged()

    val trendingSeriesUserData = userPreferences.data
        .map {
            TrendingSeriesUserData(
                trendingSeriesFavoriteIds = it.trendingSeriesFavoriteIdsMap.keys,
            )
        }
        .distinctUntilChanged()

    val gameUserData = userPreferences.data
        .map {
            GameUserData(
                gameFavoriteIds = it.gameFavoriteIdsMap.keys,
            )
        }
        .distinctUntilChanged()

    suspend fun setGameFavoriteIds(gameId: Int, isFavorite: Boolean) = update {
        if (isFavorite) gameFavoriteIds.put(gameId, true) else gameFavoriteIds.remove(gameId)
    }

    suspend fun setBestsellerFavoriteIds(bestsellerId: String, isFavorite: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (isFavorite) {
                        bestsellerFavoriteIds.put(bestsellerId, true)
                    } else {
                        bestsellerFavoriteIds.remove(bestsellerId)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("EcsPreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setGutenbergFavoriteIds(gutenbergId: Int, isFavorite: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (isFavorite) {
                        gutenbergFavoriteIds.put(gutenbergId, true)
                    } else {
                        gutenbergFavoriteIds.remove(gutenbergId)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("EcsPreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setTrendingMovieFavoriteIds(movieId: Int, isFavorite: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (isFavorite) {
                        trendingMovieFavoriteIds.put(movieId, true)
                    } else {
                        trendingMovieFavoriteIds.remove(movieId)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("EcsPreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setTrendingSeriesFavoriteIds(seriesId: Int, isFavorite: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (isFavorite) {
                        trendingSeriesFavoriteIds.put(seriesId, true)
                    } else {
                        trendingSeriesFavoriteIds.remove(seriesId)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("EcsPreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setNyTimesArticlesFavoriteIds(articleId: String, isFavorite: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (isFavorite) {
                        nyTimesArticlesFavoriteIds.put(articleId, true)
                    } else {
                        nyTimesArticlesFavoriteIds.remove(articleId)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("EcsPreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setAnimeFavoriteIds(animeId: Int, isFavorite: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (isFavorite) {
                        animeFavoriteIds.put(animeId, true)
                    } else {
                        animeFavoriteIds.remove(animeId)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("EcsPreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setMangaFavoriteIds(mangaId: Int, isFavorite: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (isFavorite) {
                        mangaFavoriteIds.put(mangaId, true)
                    } else {
                        mangaFavoriteIds.remove(mangaId)
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("EcsPreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setUsername(name: String) {
        try {
            userPreferences.updateData {
                it.copy {
                    userName = name
                }
            }
        } catch (e: IOException) {
            Log.e("EcsPreferences", "Failed to update user preferences", e)
        }
    }

    suspend fun setThemeConfig(themeConfig: ThemeConfig) = update {
        theme = when (themeConfig) {
            ThemeConfig.FOLLOW_SYSTEM -> THEME_FOLLOW_SYSTEM
            ThemeConfig.LIGHT -> THEME_LIGHT
            ThemeConfig.DARK -> THEME_DARK
        }
    }

    suspend fun setSyncEnabled(enabled: Boolean) = update { syncDisabled = !enabled }

    suspend fun setSyncIntervalHours(hours: Int) = update { syncIntervalHours = hours }

    suspend fun setNotificationsEnabled(category: NotificationCategory, enabled: Boolean) = update {
        when (category) {
            NotificationCategory.TRENDING_MOVIES -> notificationsDisabledMovies = !enabled
            NotificationCategory.TRENDING_SERIES -> notificationsDisabledSeries = !enabled
            NotificationCategory.ARTICLES -> notificationsDisabledArticles = !enabled
            NotificationCategory.BESTSELLERS -> notificationsDisabledBestsellers = !enabled
            NotificationCategory.GAMES -> notificationsDisabledGames = !enabled
        }
    }

    /** When background sync last completed successfully, in epoch millis (0 = never). */
    suspend fun lastSyncEpochMillis(): Long = userPreferences.data.first().lastSyncEpochMillis

    suspend fun setLastSyncEpochMillis(millis: Long) = update { lastSyncEpochMillis = millis }

    /** Ids of movies with a release-day reminder. */
    val releaseReminderIds = userPreferences.data
        .map { it.releaseRemindersMap.keys }
        .distinctUntilChanged()

    suspend fun setReleaseReminder(movieId: Int, title: String, releaseDate: String) = update {
        releaseReminders.put(
            movieId,
            releaseReminder {
                this.title = title
                this.releaseDate = releaseDate
            },
        )
    }

    suspend fun releaseReminderIdsNow(): Set<Int> = releaseReminderIds.first()

    suspend fun removeReleaseReminder(movieId: Int) = update { releaseReminders.remove(movieId) }

    /** When [list] was last downloaded, in epoch millis (0 = never). See [ListCache]. */
    suspend fun listRefreshedAt(list: String): Long =
        userPreferences.data.first().listRefreshedAtMap[list] ?: 0L

    suspend fun setListRefreshedAt(list: String, millis: Long) = update {
        listRefreshedAt.put(list, millis)
    }

    /** Removes every list key starting with [prefix], then sets [list] to [millis]. */
    suspend fun replaceListRefreshedAt(prefix: String, list: String, millis: Long) = update {
        listRefreshedAt.keys.filter { it.startsWith(prefix) }.forEach { listRefreshedAt.remove(it) }
        listRefreshedAt.put(list, millis)
    }

    // Ids seen by the last background sync, per category. The sync replaces each set with the ids
    // of the current first page, so "new" content is what wasn't in the previous set.

    suspend fun notifiedTrendingMovieIds(): Set<Int> =
        userPreferences.data.first().notifiedTrendingMoviesIdsMap.keys

    suspend fun setNotifiedTrendingMovieIds(ids: Set<Int>) = update {
        notifiedTrendingMoviesIds.clear()
        notifiedTrendingMoviesIds.putAll(ids.associateWith { true })
    }

    suspend fun notifiedTrendingSeriesIds(): Set<Int> =
        userPreferences.data.first().notifiedTrendingSeriesIdsMap.keys

    suspend fun setNotifiedTrendingSeriesIds(ids: Set<Int>) = update {
        notifiedTrendingSeriesIds.clear()
        notifiedTrendingSeriesIds.putAll(ids.associateWith { true })
    }

    suspend fun notifiedArticleIds(): Set<String> =
        userPreferences.data.first().notifiedArticlesIdsMap.keys

    suspend fun setNotifiedArticleIds(ids: Set<String>) = update {
        notifiedArticlesIds.clear()
        notifiedArticlesIds.putAll(ids.associateWith { true })
    }

    /** Bestseller ids are stored as "genre|id" so each genre's set is replaced independently. */
    suspend fun notifiedBestsellerIds(genre: String): Set<String> =
        userPreferences.data.first().notifiedBestsellerIdsMap.keys
            .filter { it.startsWith("$genre|") }
            .map { it.removePrefix("$genre|") }
            .toSet()

    suspend fun setNotifiedBestsellerIds(genre: String, ids: Set<String>) = update {
        val others = notifiedBestsellerIds.filter { (key, _) -> !key.startsWith("$genre|") }
        notifiedBestsellerIds.clear()
        notifiedBestsellerIds.putAll(others + ids.associate { "$genre|$it" to true })
    }

    suspend fun notifiedComingSoonGameIds(): Set<Int> =
        userPreferences.data.first().notifiedComingSoonGameIdsMap.keys

    suspend fun setNotifiedComingSoonGameIds(ids: Set<Int>) = update {
        notifiedComingSoonGameIds.clear()
        notifiedComingSoonGameIds.putAll(ids.associateWith { true })
    }

    private suspend fun update(block: UserPreferencesKt.Dsl.() -> Unit) {
        try {
            userPreferences.updateData { it.copy(block) }
        } catch (e: IOException) {
            Log.e("EcsPreferences", "Failed to update user preferences", e)
        }
    }
}

private const val THEME_DARK = 0
private const val THEME_LIGHT = 1
private const val THEME_FOLLOW_SYSTEM = 2
