package com.rick.ecatalogs.settings.data_settings.data.repository

import com.rick.ecatalogs.settings.data_settings.model.NotificationCategory
import com.rick.ecatalogs.settings.data_settings.model.SettingsUserData
import com.rick.ecatalogs.settings.data_settings.model.ThemeConfig
import kotlinx.coroutines.flow.Flow

interface UserSettingsDataRepository {

    val userData: Flow<SettingsUserData>

    suspend fun setUserName(name: String)

    suspend fun setThemeConfig(themeConfig: ThemeConfig)

    suspend fun setSyncEnabled(enabled: Boolean)

    suspend fun setSyncIntervalHours(hours: Int)

    suspend fun setNotificationsEnabled(category: NotificationCategory, enabled: Boolean)
}
