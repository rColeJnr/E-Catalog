package com.rick.ecatalogs.settings.screen_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.ecatalogs.data.util.SyncManager
import com.rick.ecatalogs.settings.data_settings.data.repository.UserSettingsDataRepository
import com.rick.ecatalogs.settings.data_settings.model.NotificationCategory
import com.rick.ecatalogs.settings.data_settings.model.SettingsUserData
import com.rick.ecatalogs.settings.data_settings.model.ThemeConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: UserSettingsDataRepository,
    private val syncManager: SyncManager,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = settingsRepository.userData
        .map<SettingsUserData, SettingsUiState>(SettingsUiState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState.Loading,
        )

    fun setUserName(name: String) {
        viewModelScope.launch { settingsRepository.setUserName(name.trim()) }
    }

    fun setThemeConfig(themeConfig: ThemeConfig) {
        viewModelScope.launch { settingsRepository.setThemeConfig(themeConfig) }
    }

    fun setNotificationsEnabled(category: NotificationCategory, enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setNotificationsEnabled(category, enabled) }
    }

    fun setSyncEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSyncEnabled(enabled)
            val interval = settingsRepository.userData.first().syncIntervalHours
            syncManager.setPeriodicSync(enabled, interval)
        }
    }

    fun setSyncIntervalHours(hours: Int) {
        viewModelScope.launch {
            settingsRepository.setSyncIntervalHours(hours)
            if (settingsRepository.userData.first().syncEnabled) {
                syncManager.setPeriodicSync(enabled = true, intervalHours = hours)
            }
        }
    }
}

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val settings: SettingsUserData) : SettingsUiState
}
