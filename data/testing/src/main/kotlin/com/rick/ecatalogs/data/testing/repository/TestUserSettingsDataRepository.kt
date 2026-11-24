package com.rick.ecatalogs.data.testing.repository

import com.rick.ecatalogs.settings.data_settings.data.repository.UserSettingsDataRepository
import com.rick.ecatalogs.settings.data_settings.model.NotificationCategory
import com.rick.ecatalogs.settings.data_settings.model.SettingsUserData
import com.rick.ecatalogs.settings.data_settings.model.ThemeConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update

val emptySettingsUserData = SettingsUserData(userName = "")

class TestUserSettingsDataRepository : UserSettingsDataRepository {

    /**
     * The backing hot flow for the settings for testing. Emits nothing until [setUserData].
     */
    private val _userData = MutableStateFlow<SettingsUserData?>(null)

    private val current get() = _userData.value ?: emptySettingsUserData

    override val userData: Flow<SettingsUserData> = _userData.filterNotNull()

    override suspend fun setUserName(name: String) =
        _userData.update { current.copy(userName = name) }

    override suspend fun setThemeConfig(themeConfig: ThemeConfig) =
        _userData.update { current.copy(themeConfig = themeConfig) }

    override suspend fun setSyncEnabled(enabled: Boolean) =
        _userData.update { current.copy(syncEnabled = enabled) }

    override suspend fun setSyncIntervalHours(hours: Int) =
        _userData.update { current.copy(syncIntervalHours = hours) }

    override suspend fun setNotificationsEnabled(category: NotificationCategory, enabled: Boolean) =
        _userData.update {
            val categories = current.enabledNotifications
            current.copy(enabledNotifications = if (enabled) categories + category else categories - category)
        }

    /**
     * A test-only API to allow setting of user data directly.
     */
    fun setUserData(userData: SettingsUserData) {
        _userData.value = userData
    }
}
