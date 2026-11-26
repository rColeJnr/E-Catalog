package com.rick.ecatalogs.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import com.rick.ecatalogs.MainActivity
import com.rick.ecatalogs.R
import com.rick.ecatalogs.auth.TestAuthSession
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import com.rick.ecatalogs.anime.screen_anime.anime_catalog.R as AnimeCatalogR
import com.rick.ecatalogs.anime.screen_anime.manga_catalog.R as MangaCatalogR
import com.rick.ecatalogs.book.screen_book.bestseller_catalog.R as BestsellerCatalogR
import com.rick.ecatalogs.book.screen_book.gutenberg_catalog.R as GutenbergCatalogR
import com.rick.ecatalogs.data.ui_components.auth.R as LoginR
import com.rick.ecatalogs.game.screen_game.game_catalog.R as GameCatalogR
import com.rick.ecatalogs.movie.screen_movie.article_catalog.R as ArticleCatalogR
import com.rick.ecatalogs.movie.screen_movie.trending_movie_catalog.R as MovieCatalogR
import com.rick.ecatalogs.movie.screen_movie.trending_series_catalog.R as SeriesCatalogR
import com.rick.ecatalogs.settings.screen_settings.R as SettingsR

/**
 * The app shell's navigation (demo flavor, no network needed):
 * - signed in → Trending series (home); signed out → login screen
 * - every drawer item opens its top-level screen, with the activity toolbar and its title
 * - Back on a top-level screen goes home; Back on home leaves the app
 * - the drawer header opens Settings; signing out there shows the login screen, and Back leaves
 */
@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val notificationPermission = grantNotificationPermission()

    @get:Rule(order = 2)
    val composeRule = createEmptyComposeRule()

    @Inject
    lateinit var authSession: TestAuthSession

    @Before
    fun setUp() {
        hiltRule.inject()
        useTestWorkManager()
    }

    private fun launchApp(): ActivityScenario<MainActivity> =
        ActivityScenario.launch(MainActivity::class.java)

    @Test
    fun signedIn_whenAppStarts_thenTrendingSeriesIsShown() {
        launchApp().use { app ->
            app.assertDestination(HOME)
            assertToolbarTitle(R.string.trending_series)
        }
    }

    @Test
    fun signedOut_whenAppStarts_thenLoginScreenIsShown() {
        authSession.isSignedIn = false
        launchApp().use {
            assertLoginScreenShown()
        }
    }

    @Test
    fun whenDrawerItemSelected_thenItsScreenIsShownWithItsTitle() {
        launchApp().use { app ->
            TOP_LEVEL_SCREENS.forEach { (title, destination) ->
                app.selectDrawerItem(title)
                app.assertDestination(destination)
                assertToolbarTitle(title)
            }
        }
    }

    @Test
    fun whenDrawerItemSelected_thenDrawerCloses() {
        launchApp().use { app ->
            app.selectDrawerItem(R.string.anime)
            app.assertDrawerOpen(false)
        }
    }

    @Test
    fun topLevelScreen_whenHamburgerTapped_thenDrawerOpens() {
        launchApp().use { app ->
            app.selectDrawerItem(R.string.anime)
            onView(withContentDescription(androidx.navigation.ui.R.string.nav_app_bar_open_drawer_description))
                .perform(click())
            app.assertDrawerOpen(true)
        }
    }

    @Test
    fun topLevelScreen_whenBackPressed_thenTrendingSeriesIsShown() {
        launchApp().use { app ->
            TOP_LEVEL_SCREENS.filter { it.second != HOME }.forEach { (title, _) ->
                app.selectDrawerItem(title)
                pressBack()
                app.assertDestination(HOME)
            }
        }
    }

    @Test
    fun severalTopLevelScreens_whenBackPressed_thenTrendingSeriesIsShown() {
        launchApp().use { app ->
            app.selectDrawerItem(R.string.anime)
            app.selectDrawerItem(R.string.manga)
            app.selectDrawerItem(R.string.games)
            pressBack()
            app.assertDestination(HOME)
        }
    }

    @Test
    fun trendingSeries_whenBackPressed_thenAppCloses() {
        launchApp().use { app ->
            pressBackUnconditionally()
            app.assertClosed()
        }
    }

    @Test
    fun whenDrawerHeaderTapped_thenSettingsIsShown() {
        launchApp().use { app ->
            app.openSettings()
            app.assertDestination(SETTINGS)
            assertToolbarTitle(R.string.settings)
            pressBack()
            app.assertDestination(HOME)
        }
    }

    @Test
    fun settings_whenSignOutTapped_thenLoginScreenIsShownAndBackLeavesTheApp() {
        launchApp().use { app ->
            app.openSettings()
            composeRule.onNodeWithText(string(SettingsR.string.settings_screen_settings_sign_out))
                .performScrollTo()
                .performClick()

            assertFalse(authSession.isSignedIn)
            assertLoginScreenShown()

            pressBackUnconditionally()
            app.assertClosed()
        }
    }

    private fun assertLoginScreenShown() {
        composeRule.onNodeWithText(string(LoginR.string.data_ui_components_auth_catalogs))
            .assertIsDisplayed()
    }

    private fun ActivityScenario<MainActivity>.openSettings() {
        openDrawer()
        onView(allOf(withId(R.id.name), isDescendantOfA(withId(R.id.nav_view)))).perform(click())
        awaitDrawer(open = false)
    }

    // The hamburger opens the drawer with its slide animation: wait for it to settle
    private fun ActivityScenario<MainActivity>.assertDrawerOpen(open: Boolean) = awaitDrawer(open)

    private fun ActivityScenario<MainActivity>.assertClosed() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        assertTrue(state == Lifecycle.State.DESTROYED, "MainActivity should have finished, was $state")
    }

    private companion object {
        val HOME = SeriesCatalogR.id.movie_screen_movie_trending_series_catalog_trendingseriesfragment
        val SETTINGS = SettingsR.id.settings_screen_settings_settingsfragment

        /** Drawer title → the screen it opens, in drawer order. */
        val TOP_LEVEL_SCREENS = listOf(
            R.string.trending_series to HOME,
            R.string.trending_movies to MovieCatalogR.id.movie_screen_movie_trending_movie_catalog_trendingmoviefragment,
            R.string.movie_reviews to ArticleCatalogR.id.movie_screen_movie_article_catalog_articlefragment,
            R.string.anime to AnimeCatalogR.id.anime_screen_anime_anime_catalog_animecatalogfragment,
            R.string.manga to MangaCatalogR.id.anime_screen_anime_manga_catalog_mangacatalogfragment,
            R.string.nyt_bestseller to BestsellerCatalogR.id.book_screen_book_bestseller_catalog_bestsellerfragment,
            R.string.the_gutenberg_project to GutenbergCatalogR.id.book_screen_book_gutenberg_catalog_gutenbergcatalogfragment,
            R.string.games to GameCatalogR.id.game_screen_game_game_catalog_gamecatalogfragment,
        )
    }
}
