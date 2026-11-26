package com.rick.ecatalogs.ui.catalog

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.rick.ecatalogs.MainActivity
import com.rick.ecatalogs.ui.assertDestination
import com.rick.ecatalogs.ui.assertToolbarTitle
import com.rick.ecatalogs.ui.grantNotificationPermission
import com.rick.ecatalogs.ui.selectDrawerItem
import com.rick.ecatalogs.ui.useTestWorkManager
import dagger.hilt.android.testing.HiltAndroidRule
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue
import com.rick.ecatalogs.movie.screen_movie.trending_series_search.R as SearchR

/**
 * The flow every XML content area shares (demo flavor, signed in, default preferences):
 * 1. the catalog (from the drawer) lists the demo items
 * 2. tapping an item opens its details without the activity toolbar; Back returns to the catalog
 * 3. Back from details keeps the catalog's scroll position
 * 4. the search action opens search; submitting a query lists matching results
 * 5. a favorited item shows in the area's favorites screen
 *
 * Each area's subclass says where things are; areas that differ override [openItemAndAssertDetails]
 * or [favorite].
 */
abstract class CatalogFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val notificationPermission = grantNotificationPermission()

    @get:Rule(order = 2)
    val composeRule = createEmptyComposeRule()

    protected lateinit var app: ActivityScenario<MainActivity>

    @get:StringRes
    abstract val drawerTitle: Int

    @get:IdRes
    abstract val catalogDestination: Int

    abstract val catalogList: ListSpec

    /** The details screen an item opens. */
    @get:IdRes
    abstract val detailsDestination: Int

    /** The catalog's search action, or null when the area has no search. */
    @get:IdRes
    abstract val searchItem: Int?

    @get:IdRes
    open val searchDestination: Int = 0

    /** A query the demo search data has results for (matched against titles). */
    open val searchQuery: String = ""

    open val searchResults: ListSpec? = null

    @get:IdRes
    abstract val favoritesItem: Int

    @get:IdRes
    abstract val favoritesDestination: Int

    @Before
    fun launchCatalog() {
        hiltRule.inject()
        useTestWorkManager()
        app = ActivityScenario.launch(MainActivity::class.java)
        app.selectDrawerItem(drawerTitle)
        app.assertDestination(catalogDestination)
    }

    @After
    fun closeApp() = app.close()

    @Test
    fun catalog_whenOpened_thenDemoItemsAreListed() {
        assertToolbarTitle(drawerTitle)
        // Tall headers (anime, manga) push the first cards below the fold: scroll to the second
        app.scrollToItem(catalogList, index = 1)
    }

    @Test
    fun catalogItem_whenTapped_thenDetailsOpenAndBackReturnsToCatalog() {
        val title = app.scrollToItem(catalogList, index = 0)

        openItemAndAssertDetails(title)
        pressBack()

        app.assertDestination(catalogDestination)
        app.awaitCards(catalogList)
    }

    @Test
    fun scrolledCatalog_whenBackFromDetails_thenScrollPositionIsKept() {
        // Far enough down that a jump back to the top would hide it
        val title = app.scrollToItem(catalogList, index = SCROLLED_INDEX)
        assertCardDisplayed(catalogList, title)
        var before = 0 to 0
        app.onActivity { before = it.scrollPosition(catalogList) }
        assertTrue(before.first > 0 || before.second > 0, "the list should be scrolled, was at $before")

        openItemAndAssertDetails(title)
        pressBack()

        app.assertDestination(catalogDestination)
        app.waitUntil("the list back at $before (first visible position, outer scroll)", timeoutMillis = 5_000) {
            it.scrollPosition(catalogList) == before
        }
    }

    @Test
    fun catalog_whenSearchSubmitted_thenMatchingResultsAreListed() {
        val search = searchItem
        assumeTrue("This area has no search screen", search != null)
        val results = checkNotNull(searchResults)

        app.clickToolbarItem(search!!)
        app.assertDestination(searchDestination)
        assertActivityToolbarHidden()

        // The keyboard's own action button, as a user submits
        onView(allOf(withId(SearchR.id.search_input), isDisplayed()))
            .perform(click(), typeText(searchQuery), pressImeActionButton())

        app.waitUntil("results for \"$searchQuery\"") { activity ->
            activity.cardTitles(results).any { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    @Test
    fun catalogItem_whenFavorited_thenItIsListedInFavorites() {
        val title = app.scrollToItem(catalogList, index = 0)

        favorite(title)
        app.clickToolbarItem(favoritesItem)

        app.assertDestination(favoritesDestination)
        // Some lists change the case (NYT titles are upper case in the data)
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText(title, substring = true, ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    /** Opens the item titled [title] from the catalog and checks its details screen is shown. */
    protected open fun openItemAndAssertDetails(title: String) {
        openCard(catalogList, title)
        app.assertDestination(detailsDestination)
        assertActivityToolbarHidden()
    }

    /** Marks the catalog item titled [title] as a favorite; the catalog is shown afterwards. */
    protected open fun favorite(title: String) {
        favoriteCard(catalogList, title)
    }

    protected companion object {
        const val SCROLLED_INDEX = 6
    }
}
