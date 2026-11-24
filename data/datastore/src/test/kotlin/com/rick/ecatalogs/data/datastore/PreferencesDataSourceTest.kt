package com.rick.ecatalogs.data.datastore

import com.rick.ecatalogs.data.datastore.test.InMemoryDataStore
import com.rick.ecatalogs.settings.data_settings.model.DEFAULT_SYNC_INTERVAL_HOURS
import com.rick.ecatalogs.settings.data_settings.model.NotificationCategory
import com.rick.ecatalogs.settings.data_settings.model.SYNC_INTERVAL_MONTH_HOURS
import com.rick.ecatalogs.settings.data_settings.model.SYNC_INTERVAL_WEEK_HOURS
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

class PreferencesDataSourceTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: PreferencesDataSource

    @Before
    fun setup() {
        subject = PreferencesDataSource(InMemoryDataStore(UserPreferences.getDefaultInstance()))
    }

    @Test
    fun themeConfigIsDarkByDefault() = testScope.runTest {
        assertEquals(ThemeConfig.DARK, subject.settingsUserData.first().themeConfig)
    }

    @Test
    fun themeConfigIsStoredForEveryOption() = testScope.runTest {
        ThemeConfig.entries.forEach { config ->
            subject.setThemeConfig(config)
            assertEquals(config, subject.settingsUserData.first().themeConfig)
        }
    }

    @Test
    fun syncIsEnabledEveryDefaultIntervalByDefault() = testScope.runTest {
        val settings = subject.settingsUserData.first()
        assertTrue(settings.syncEnabled)
        assertEquals(DEFAULT_SYNC_INTERVAL_HOURS, settings.syncIntervalHours)
    }

    @Test
    fun syncSettingsAreStored() = testScope.runTest {
        subject.setSyncEnabled(false)
        subject.setSyncIntervalHours(SYNC_INTERVAL_MONTH_HOURS)

        val settings = subject.settingsUserData.first()
        assertFalse(settings.syncEnabled)
        assertEquals(SYNC_INTERVAL_MONTH_HOURS, settings.syncIntervalHours)
    }

    @Test
    fun syncIntervalIsWeeklyByDefault() = testScope.runTest {
        assertEquals(SYNC_INTERVAL_WEEK_HOURS, subject.settingsUserData.first().syncIntervalHours)
    }

    @Test
    fun lastSyncTime_isZeroUntilSet() = testScope.runTest {
        assertEquals(0L, subject.lastSyncEpochMillis())
        subject.setLastSyncEpochMillis(1_700_000_000_000)
        assertEquals(1_700_000_000_000, subject.lastSyncEpochMillis())
    }

    @Test
    fun allNotificationsAreEnabledByDefault() = testScope.runTest {
        assertEquals(
            NotificationCategory.entries.toSet(),
            subject.settingsUserData.first().enabledNotifications,
        )
    }

    @Test
    fun disablingANotificationCategory_leavesTheOthersEnabled() = testScope.runTest {
        subject.setNotificationsEnabled(NotificationCategory.ARTICLES, false)

        assertEquals(
            NotificationCategory.entries.toSet() - NotificationCategory.ARTICLES,
            subject.settingsUserData.first().enabledNotifications,
        )

        subject.setNotificationsEnabled(NotificationCategory.ARTICLES, true)
        assertTrue(NotificationCategory.ARTICLES in subject.settingsUserData.first().enabledNotifications)
    }

    @Test
    fun userNameIsStored() = testScope.runTest {
        subject.setUsername("rick")
        assertEquals("rick", subject.settingsUserData.first().userName)
    }

    @Test
    fun favoriteIdsAreAddedAndRemoved() = testScope.runTest {
        subject.setTrendingMovieFavoriteIds(1, true)
        subject.setTrendingMovieFavoriteIds(2, true)
        subject.setTrendingMovieFavoriteIds(1, false)

        assertEquals(setOf(2), subject.trendingMovieUserData.first().trendingMovieFavoriteIds)
    }

    @Test
    fun favoritesOfOneCatalog_doNotAffectAnother() = testScope.runTest {
        subject.setAnimeFavoriteIds(7, true)
        subject.setNyTimesArticlesFavoriteIds("article", true)

        assertEquals(setOf(7), subject.animeUserData.first().animeFavoriteIds)
        assertTrue(subject.mangaUserData.first().mangaFavoriteIds.isEmpty())
        assertEquals(setOf("article"), subject.articleUserData.first().nyTimesArticlesFavoriteIds)
    }

    @Test
    fun notifiedIdsAreReplacedNotMerged() = testScope.runTest {
        subject.setNotifiedTrendingMovieIds(setOf(1, 2))
        subject.setNotifiedTrendingMovieIds(setOf(2, 3))

        assertEquals(setOf(2, 3), subject.notifiedTrendingMovieIds())
    }

    @Test
    fun notifiedBestsellerIdsAreKeptPerGenre() = testScope.runTest {
        subject.setNotifiedBestsellerIds("fiction", setOf("a", "b"))
        subject.setNotifiedBestsellerIds("nonfiction", setOf("c"))
        subject.setNotifiedBestsellerIds("fiction", setOf("d"))

        assertEquals(setOf("d"), subject.notifiedBestsellerIds("fiction"))
        assertEquals(setOf("c"), subject.notifiedBestsellerIds("nonfiction"))
    }
}
