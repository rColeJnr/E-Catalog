package com.rick.settings.data_settings.data.repository

import com.rick.settings.data_settings.model.SettingsUserData
import kotlinx.coroutines.flow.Flow

interface UserSettingsDataRepository {

    val userData: Flow<SettingsUserData>

    suspend fun setUserName(name: String)

}