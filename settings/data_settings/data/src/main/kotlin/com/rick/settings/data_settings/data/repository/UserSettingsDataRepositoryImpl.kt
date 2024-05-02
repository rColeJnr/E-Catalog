package com.rick.settings.data_settings.data.repository

import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.datastore.PreferencesDataSource
import com.rick.settings.data_settings.model.SettingsUserData
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
}