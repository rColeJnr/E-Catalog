package com.rick.ecatalogs.settings.data_settings.data.repository

import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.settings.data_settings.model.NotificationCategory
import com.rick.ecatalogs.settings.data_settings.model.SettingsUserData
import com.rick.ecatalogs.settings.data_settings.model.ThemeConfig
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserSettingsDataRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource,
    private val analyticsHelper: AnalyticsHelper
) : UserSettingsDataRepository {

    override val userData: Flow<SettingsUserData>
        get() = preferencesDataSource.settingsUserData

    override suspend fun setUserName(name: String) {
        preferencesDataSource.setUsername(name)
        analyticsHelper.logUsernameChanged(name)
    }

    override suspend fun setThemeConfig(themeConfig: ThemeConfig) =
        preferencesDataSource.setThemeConfig(themeConfig)

    override suspend fun setSyncEnabled(enabled: Boolean) =
        preferencesDataSource.setSyncEnabled(enabled)

    override suspend fun setSyncIntervalHours(hours: Int) =
        preferencesDataSource.setSyncIntervalHours(hours)

    override suspend fun setNotificationsEnabled(category: NotificationCategory, enabled: Boolean) =
        preferencesDataSource.setNotificationsEnabled(category, enabled)
}
