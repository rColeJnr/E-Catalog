package com.rick.ecatalogs.ui

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.rick.ecatalogs.LAUNCH_PREFS
import com.rick.ecatalogs.MainActivity
import com.rick.ecatalogs.R
import com.rick.ecatalogs.auth.TestAuthSession
import com.rick.ecatalogs.settings.data_settings.data.repository.UserSettingsDataRepository
import com.rick.ecatalogs.settings.data_settings.model.NotificationCategory
import com.rick.ecatalogs.settings.data_settings.model.SYNC_INTERVAL_MONTH_HOURS
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import com.rick.ecatalogs.data.ui_components.auth.R as LoginR
import com.rick.ecatalogs.settings.screen_settings.R as SettingsR

/**
 * Settings, opened from the drawer header. Each test starts from default preferences
 * (TestDataStoreModule): no username, dark theme, weekly sync on, every notification on.
 */
@HiltAndroidTest
class SettingsTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val notificationPermission = grantNotificationPermission()

    @get:Rule(order = 2)
    val composeRule = createEmptyComposeRule()

    @Inject
    lateinit var authSession: TestAuthSession

    @Inject
    lateinit var settingsRepository: UserSettingsDataRepository

    private lateinit var scenario: ActivityScenario<MainActivity>

    // The theme test changes the app-wide night mode and the launch preference; put them back.
    private val launchPrefs = InstrumentationRegistry.getInstrumentation().targetContext
        .getSharedPreferences(LAUNCH_PREFS, Context.MODE_PRIVATE)
    private var savedNightMode = AppCompatDelegate.MODE_NIGHT_YES
    private var savedLaunchPrefs: Map<String, *> = emptyMap<String, Any>()

    @Before
    fun setUp() {
        hiltRule.inject()
        useTestWorkManager()
        savedNightMode = AppCompatDelegate.getDefaultNightMode()
        savedLaunchPrefs = launchPrefs.all.toMap()

        scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.selectSettings()
        scenario.assertDestination(SettingsR.id.settings_screen_settings_settingsfragment)
    }

    @After
    fun tearDown() {
        scenario.close()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            AppCompatDelegate.setDefaultNightMode(savedNightMode)
        }
        launchPrefs.edit().apply {
            clear()
            savedLaunchPrefs.forEach { (key, value) ->
                when (value) {
                    is Int -> putInt(key, value)
                    is Boolean -> putBoolean(key, value)
                    is String -> putString(key, value)
                    is Long -> putLong(key, value)
                }
            }
        }.commit()
    }

    // Username

    @Test
    fun whenUsernameChangedAndSaved_thenGreetingAndDrawerHeaderShowIt() {
        text(SettingsR.string.settings_screen_settings_change_username).performClick()
        composeRule.onNode(hasSetTextAction() and hasText(string(SettingsR.string.settings_screen_settings_username)))
            .performTextReplacement("rick")
        text(SettingsR.string.settings_screen_settings_save).performClick()

        composeRule.onNodeWithText(string(SettingsR.string.settings_screen_settings_greeting, "rick"))
            .assertIsDisplayed()
        assertEquals("rick", settings().userName)

        scenario.openDrawer()
        onView(allOf(withId(R.id.name), isDescendantOfA(withId(R.id.nav_view))))
            .check(matches(withText("rick")))
    }

    // Delete account

    @Test
    fun whenDeleteAccountTapped_thenConfirmationIsShown_andCancelKeepsTheAccount() {
        deleteAccountButton().performClick()
        text(SettingsR.string.settings_screen_settings_delete_account_message).assertIsDisplayed()

        text(SettingsR.string.settings_screen_settings_cancel).performClick()

        text(SettingsR.string.settings_screen_settings_delete_account_message).assertDoesNotExist()
        assertFalse(authSession.deletedAccount)
        assertTrue(authSession.isSignedIn)
        scenario.assertDestination(SettingsR.id.settings_screen_settings_settingsfragment)
    }

    @Test
    fun whenDeleteConfirmed_thenAccountIsDeletedAndLoginScreenIsShown() {
        deleteAccountButton().performClick()
        text(SettingsR.string.settings_screen_settings_delete).performClick()

        assertTrue(authSession.deletedAccount)
        text(LoginR.string.data_ui_components_auth_catalogs).assertIsDisplayed()
    }

    // Theme

    @Test
    fun whenThemeChipSelected_thenAppUsesThatThemeAndChipIsSelected() {
        text(SettingsR.string.settings_screen_settings_theme_dark).performScrollTo().assertIsSelected()

        text(SettingsR.string.settings_screen_settings_theme_light).performScrollTo().performClick()

        // Changing the night mode recreates the activity; the compose rule follows the new one
        text(SettingsR.string.settings_screen_settings_theme_light).performScrollTo().assertIsSelected()
        text(SettingsR.string.settings_screen_settings_theme_dark).assertIsNotSelected()
        assertEquals(AppCompatDelegate.MODE_NIGHT_NO, AppCompatDelegate.getDefaultNightMode())

        text(SettingsR.string.settings_screen_settings_theme_system).performScrollTo().performClick()
        assertEquals(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, AppCompatDelegate.getDefaultNightMode())
    }

    // Background sync

    @Test
    fun whenSyncSwitchedOff_thenIntervalIsHidden_andOnShowsItAgain() {
        val syncSwitch = text(SettingsR.string.settings_screen_settings_sync_enabled)
        syncSwitch.performScrollTo().assertIsOn()
        text(SettingsR.string.settings_screen_settings_sync_interval).assertExists()

        syncSwitch.performClick()
        syncSwitch.assertIsOff()
        text(SettingsR.string.settings_screen_settings_sync_interval).assertDoesNotExist()
        assertFalse(settings().syncEnabled)

        syncSwitch.performClick()
        syncSwitch.assertIsOn()
        text(SettingsR.string.settings_screen_settings_sync_interval).assertExists()
        assertTrue(settings().syncEnabled)
    }

    @Test
    fun whenSyncIntervalChipSelected_thenItIsSavedAndSelected() {
        text(SettingsR.string.settings_screen_settings_sync_weekly).performScrollTo().assertIsSelected()

        text(SettingsR.string.settings_screen_settings_sync_monthly).performScrollTo().performClick()

        text(SettingsR.string.settings_screen_settings_sync_monthly).assertIsSelected()
        text(SettingsR.string.settings_screen_settings_sync_weekly).assertIsNotSelected()
        assertEquals(SYNC_INTERVAL_MONTH_HOURS, settings().syncIntervalHours)
    }

    // Notifications

    @Test
    fun notificationSwitches_areOnByDefault_andEachOneTurnsOffItsCategory() {
        NOTIFICATION_SWITCHES.forEach { (label, category) ->
            val switch = text(label).performScrollTo()
            switch.assertIsOn()

            switch.performClick()

            switch.assertIsOff()
            assertFalse(category in settings().enabledNotifications, "$category still enabled")
        }
        assertTrue(settings().enabledNotifications.isEmpty())
    }

    @Test
    fun notificationSwitchedOff_whenSettingsReopened_thenItIsStillOff() {
        text(SettingsR.string.settings_screen_settings_games).performScrollTo().performClick()

        scenario.selectDrawerItem(R.string.anime)
        scenario.selectSettings()

        text(SettingsR.string.settings_screen_settings_games).performScrollTo().assertIsOff()
        text(SettingsR.string.settings_screen_settings_trending_series).performScrollTo().assertIsOn()
    }

    private fun ActivityScenario<MainActivity>.selectSettings() {
        openDrawer()
        onView(allOf(withId(R.id.name), isDescendantOfA(withId(R.id.nav_view))))
            .perform(androidx.test.espresso.action.ViewActions.click())
        awaitDrawer(open = false)
    }

    private fun text(id: Int) = composeRule.onNodeWithText(string(id))

    // "Delete account" is both the button and the dialog title; the button is the only one
    // before the dialog opens.
    private fun deleteAccountButton() =
        composeRule.onAllNodesWithText(string(SettingsR.string.settings_screen_settings_delete_account))[0]

    private fun settings() = runBlocking { settingsRepository.userData.first() }

    private companion object {
        val NOTIFICATION_SWITCHES = listOf(
            SettingsR.string.settings_screen_settings_trending_movies to NotificationCategory.TRENDING_MOVIES,
            SettingsR.string.settings_screen_settings_trending_series to NotificationCategory.TRENDING_SERIES,
            SettingsR.string.settings_screen_settings_movie_reviews to NotificationCategory.ARTICLES,
            SettingsR.string.settings_screen_settings_bestsellers to NotificationCategory.BESTSELLERS,
            SettingsR.string.settings_screen_settings_games to NotificationCategory.GAMES,
            SettingsR.string.settings_screen_settings_anime to NotificationCategory.ANIME,
            SettingsR.string.settings_screen_settings_manga to NotificationCategory.MANGA,
        )
    }
}
