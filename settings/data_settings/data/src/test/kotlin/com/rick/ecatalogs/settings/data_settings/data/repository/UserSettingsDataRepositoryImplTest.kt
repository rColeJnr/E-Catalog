package com.rick.ecatalogs.settings.data_settings.data.repository

import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.data.datastore.UserPreferences
import com.rick.ecatalogs.data.datastore.test.InMemoryDataStore
import com.rick.ecatalogs.data.testing.util.TestAnalyticsHelper
import com.rick.ecatalogs.settings.data_settings.model.NotificationCategory
import com.rick.ecatalogs.settings.data_settings.model.ThemeConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserSettingsDataRepositoryImplTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: UserSettingsDataRepositoryImpl

    private lateinit var preferencesDataSource: PreferencesDataSource

    private val analyticsHelper = TestAnalyticsHelper()

    @Before
    fun setup() {
        preferencesDataSource =
            PreferencesDataSource(InMemoryDataStore(UserPreferences.getDefaultInstance()))
        subject = UserSettingsDataRepositoryImpl(
            preferencesDataSource = preferencesDataSource,
            analyticsHelper = analyticsHelper,
        )
    }

    @Test
    fun userData_isBackedByPreferencesDataSource() = testScope.runTest {
        subject.setThemeConfig(ThemeConfig.LIGHT)
        subject.setSyncIntervalHours(24)
        subject.setNotificationsEnabled(NotificationCategory.BESTSELLERS, false)

        assertEquals(
            preferencesDataSource.settingsUserData.first(),
            subject.userData.first(),
        )
    }

    @Test
    fun setUserName_storesNameAndLogsEvent() = testScope.runTest {
        subject.setUserName("rick")

        assertEquals("rick", subject.userData.first().userName)
        assertTrue(analyticsHelper.hasLoggedType("username_changed"))
    }

    @Test
    fun setSyncEnabled_disablesSync() = testScope.runTest {
        subject.setSyncEnabled(false)
        assertFalse(subject.userData.first().syncEnabled)
    }
}
