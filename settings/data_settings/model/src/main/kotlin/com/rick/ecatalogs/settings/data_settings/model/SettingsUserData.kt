package com.rick.ecatalogs.settings.data_settings.model

data class SettingsUserData(
    val userName: String,
    val themeConfig: ThemeConfig = ThemeConfig.DARK,
    val syncEnabled: Boolean = true,
    val syncIntervalHours: Int = DEFAULT_SYNC_INTERVAL_HOURS,
    /** Categories whose new-content notifications are turned on. */
    val enabledNotifications: Set<NotificationCategory> = NotificationCategory.entries.toSet(),
)

enum class ThemeConfig { FOLLOW_SYSTEM, LIGHT, DARK }

/** Content categories the background sync can notify about. */
enum class NotificationCategory { TRENDING_MOVIES, TRENDING_SERIES, ARTICLES, BESTSELLERS, GAMES }

// The content behind the sync changes slowly: the NYT bestseller lists and TMDB's trending
// (weekly window) update once a week, so syncing more often than that only spends data.
const val SYNC_INTERVAL_WEEK_HOURS = 7 * 24
const val SYNC_INTERVAL_MONTH_HOURS = 30 * 24
const val DEFAULT_SYNC_INTERVAL_HOURS = SYNC_INTERVAL_WEEK_HOURS
val SYNC_INTERVAL_OPTIONS_HOURS = listOf(SYNC_INTERVAL_WEEK_HOURS, SYNC_INTERVAL_MONTH_HOURS)
