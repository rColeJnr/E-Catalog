package com.rick.ecatalogs.settings.data_settings.model

data class SettingsUserData(
    val userName: String,
    val themeConfig: ThemeConfig = ThemeConfig.FOLLOW_SYSTEM,
    val syncEnabled: Boolean = true,
    val syncIntervalHours: Int = DEFAULT_SYNC_INTERVAL_HOURS,
    /** Categories whose new-content notifications are turned on. */
    val enabledNotifications: Set<NotificationCategory> = NotificationCategory.entries.toSet(),
)

enum class ThemeConfig { FOLLOW_SYSTEM, LIGHT, DARK }

/** Content categories the background sync can notify about. */
enum class NotificationCategory { TRENDING_MOVIES, TRENDING_SERIES, ARTICLES, BESTSELLERS }

const val DEFAULT_SYNC_INTERVAL_HOURS = 12
val SYNC_INTERVAL_OPTIONS_HOURS = listOf(6, 12, 24)
