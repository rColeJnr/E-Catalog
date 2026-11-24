package com.rick.ecatalogs.settings.screen_settings

import com.rick.ecatalogs.data.testing.repository.TestUserSettingsDataRepository
import com.rick.ecatalogs.data.testing.repository.emptySettingsUserData
import com.rick.ecatalogs.data.testing.util.MainDispatcherRule
import com.rick.ecatalogs.data.testing.util.TestSyncManager
import com.rick.ecatalogs.settings.data_settings.model.NotificationCategory
import com.rick.ecatalogs.settings.data_settings.model.SYNC_INTERVAL_MONTH_HOURS
import com.rick.ecatalogs.settings.data_settings.model.ThemeConfig
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Rule
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val settingsRepository = TestUserSettingsDataRepository()
    private val syncManager = TestSyncManager()

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        viewModel = SettingsViewModel(
            settingsRepository = settingsRepository,
            syncManager = syncManager,
        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(SettingsUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun stateIsSuccessAfterUserDataLoaded() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        settingsRepository.setUserData(emptySettingsUserData.copy(userName = "rick"))

        assertEquals(
            SettingsUiState.Success(emptySettingsUserData.copy(userName = "rick")),
            viewModel.uiState.value,
        )
    }

    @Test
    fun setUserName_trimsWhitespace() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        settingsRepository.setUserData(emptySettingsUserData)

        viewModel.setUserName("  rick  ")

        assertEquals("rick", successState().userName)
    }

    @Test
    fun setThemeConfig_updatesState() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        settingsRepository.setUserData(emptySettingsUserData)

        viewModel.setThemeConfig(ThemeConfig.LIGHT)

        assertEquals(ThemeConfig.LIGHT, successState().themeConfig)
    }

    @Test
    fun setNotificationsEnabled_updatesState() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        settingsRepository.setUserData(emptySettingsUserData)

        viewModel.setNotificationsEnabled(NotificationCategory.TRENDING_SERIES, false)

        assertEquals(
            NotificationCategory.entries.toSet() - NotificationCategory.TRENDING_SERIES,
            successState().enabledNotifications,
        )
    }

    @Test
    fun disablingSync_cancelsPeriodicSync() = runTest {
        settingsRepository.setUserData(emptySettingsUserData.copy(syncIntervalHours = SYNC_INTERVAL_MONTH_HOURS))

        viewModel.setSyncEnabled(false)

        assertEquals(false to SYNC_INTERVAL_MONTH_HOURS, syncManager.periodicSync)
    }

    @Test
    fun changingInterval_whileSyncEnabled_reschedulesPeriodicSync() = runTest {
        settingsRepository.setUserData(emptySettingsUserData.copy(syncEnabled = true))

        viewModel.setSyncIntervalHours(SYNC_INTERVAL_MONTH_HOURS)

        assertEquals(true to SYNC_INTERVAL_MONTH_HOURS, syncManager.periodicSync)
    }

    @Test
    fun changingInterval_whileSyncDisabled_doesNotScheduleSync() = runTest {
        settingsRepository.setUserData(emptySettingsUserData.copy(syncEnabled = false))

        viewModel.setSyncIntervalHours(SYNC_INTERVAL_MONTH_HOURS)

        assertNull(syncManager.periodicSync)
    }

    private fun successState() = (viewModel.uiState.value as SettingsUiState.Success).settings
}
