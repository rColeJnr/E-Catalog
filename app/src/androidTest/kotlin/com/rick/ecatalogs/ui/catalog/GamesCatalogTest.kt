package com.rick.ecatalogs.ui.catalog

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.pressBack
import com.rick.ecatalogs.MainActivity
import com.rick.ecatalogs.R
import com.rick.ecatalogs.ui.assertDestination
import com.rick.ecatalogs.ui.assertToolbarTitle
import com.rick.ecatalogs.ui.grantNotificationPermission
import com.rick.ecatalogs.ui.selectDrawerItem
import com.rick.ecatalogs.ui.string
import com.rick.ecatalogs.ui.useTestWorkManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue
import com.rick.ecatalogs.game.screen_game.common.R as GameCommonR
import com.rick.ecatalogs.game.screen_game.game_catalog.R as GameCatalogR
import com.rick.ecatalogs.game.screen_game.game_details.R as GameDetailsR
import com.rick.ecatalogs.game.screen_game.game_favorites.R as GameFavoritesR
import com.rick.ecatalogs.game.screen_game.game_search.R as GameSearchR

/**
 * Games (IGDB, demo flavor: 5 games from games.json). The same flow as the other areas, but every
 * screen is Compose: the catalog is a RecyclerView whose rows are ComposeViews, under a Compose
 * header (coming soon carousel, recently released row, genre chips) that repeats some titles.
 * Favorited from the heart on a list row or on the details top bar.
 */
@HiltAndroidTest
class GamesCatalogTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val notificationPermission = grantNotificationPermission()

    @get:Rule(order = 2)
    val composeRule = createEmptyComposeRule()

    private lateinit var app: ActivityScenario<MainActivity>

    private val addFavorite get() = string(GameCommonR.string.game_screen_game_common_add_favorite)
    private val removeFavorite get() = string(GameCommonR.string.game_screen_game_common_remove_favorite)

    @Before
    fun setUp() {
        hiltRule.inject()
        useTestWorkManager()
        app = ActivityScenario.launch(MainActivity::class.java)
        app.selectDrawerItem(R.string.games)
        app.assertDestination(CATALOG)
    }

    @After
    fun tearDown() = app.close()

    @Test
    fun catalog_whenOpened_thenDemoGamesAreListed() {
        assertToolbarTitle(R.string.games)
        awaitListRow(FIRST_GAME)
    }

    @Test
    fun catalogGame_whenTapped_thenDetailsOpenAndBackReturnsToCatalog() {
        scrollToRow(0)
        listRow(FIRST_GAME).performClick()

        app.assertDestination(DETAILS)
        assertActivityToolbarHidden()
        awaitText(FIRST_GAME)

        composeRule.onNodeWithContentDescription(string(GameCommonR.string.game_screen_game_common_back))
            .performClick()
        app.assertDestination(CATALOG)
    }

    @Test
    fun scrolledCatalog_whenBackFromDetails_thenScrollPositionIsKept() {
        scrollToRow(DEMO_GAMES.lastIndex)
        val before = scrollState()
        assertTrue(before.second > 0, "the list should be scrolled, was at $before")

        listRow(DEMO_GAMES.last()).performClick()
        app.assertDestination(DETAILS)
        pressBack()

        app.assertDestination(CATALOG)
        awaitListRow(DEMO_GAMES.last())
        app.waitUntil("the list back at $before") { scrollStateOf(it) == before }
    }

    @Test
    fun catalog_whenSearchSubmitted_thenMatchingGamesAreListed() {
        app.clickToolbarItem(GameCatalogR.id.game_search)
        app.assertDestination(SEARCH)
        assertActivityToolbarHidden()

        composeRule.onNode(hasSetTextAction()).performTextInput("Elden")
        composeRule.onNode(hasSetTextAction()).performImeAction()

        awaitListRow("Elden Ring")
        composeRule.onAllNodesWithText("Stardew Valley").fetchSemanticsNodes().let {
            assertTrue(it.isEmpty(), "Stardew Valley doesn't match \"Elden\"")
        }
    }

    @Test
    fun catalogGame_whenFavoritedFromList_thenItIsListedInFavorites() {
        scrollToRow(0)
        favoriteOnRow(FIRST_GAME).performClick()
        composeRule.waitUntil(5_000) { nodes(hasContentDescription(removeFavorite)).isNotEmpty() }

        app.clickToolbarItem(GameCatalogR.id.game_favorites)

        app.assertDestination(FAVORITES)
        awaitText(FIRST_GAME)
    }

    @Test
    fun gameDetails_whenFavorited_thenItIsListedInFavorites() {
        scrollToRow(0)
        listRow(FIRST_GAME).performClick()
        app.assertDestination(DETAILS)

        composeRule.waitUntil(10_000) { nodes(hasContentDescription(addFavorite)).isNotEmpty() }
        composeRule.onNodeWithContentDescription(addFavorite).performClick()
        composeRule.waitUntil(5_000) { nodes(hasContentDescription(removeFavorite)).isNotEmpty() }
        pressBack()

        app.clickToolbarItem(GameCatalogR.id.game_favorites)
        app.assertDestination(FAVORITES)
        awaitText(FIRST_GAME)
    }

    /** A list row (not a header poster): clickable, titled [name], with its own heart. */
    private fun rowMatcher(name: String): SemanticsMatcher =
        hasClickAction() and hasText(name) and hasAnyDescendant(
            hasContentDescription(addFavorite) or hasContentDescription(removeFavorite)
        )

    private fun listRow(name: String) = composeRule.onAllNodes(rowMatcher(name)).onFirst()

    private fun favoriteOnRow(name: String) = composeRule.onAllNodes(
        hasContentDescription(addFavorite) and hasAnyAncestor(hasClickAction() and hasText(name))
    ).onFirst()

    private fun awaitListRow(name: String) {
        composeRule.waitUntil(15_000) { nodes(rowMatcher(name)).isNotEmpty() }
        listRow(name).assertIsDisplayed()
    }

    private fun awaitText(text: String) {
        composeRule.waitUntil(15_000) {
            composeRule.onAllNodesWithText(text, substring = true).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun nodes(matcher: SemanticsMatcher) =
        composeRule.onAllNodes(matcher).fetchSemanticsNodes()

    /** Scrolls the [index]-th game row (after the header) to the top; returns the first visible position. */
    private fun scrollToRow(index: Int): Int {
        app.waitUntil("${index + 1} game rows") { activity ->
            (activity.findViewById<RecyclerView>(GameCatalogR.id.game_recycler_view)?.adapter?.itemCount ?: 0) > index + 1
        }
        app.onActivity { activity ->
            val list = activity.findViewById<RecyclerView>(GameCatalogR.id.game_recycler_view)
            (list.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(index + 1, 0)
        }
        awaitListRow(DEMO_GAMES[index])
        return firstVisiblePosition()
    }

    /** (first visible position, pixels scrolled), with the offset rounded to absorb sub-pixel layout. */
    private fun scrollState(): Pair<Int, Int> {
        var state = -1 to -1
        app.onActivity { state = scrollStateOf(it) }
        return state
    }

    private fun scrollStateOf(activity: MainActivity): Pair<Int, Int> {
        val list = activity.findViewById<RecyclerView>(GameCatalogR.id.game_recycler_view)
        val layoutManager = list.layoutManager as LinearLayoutManager
        return layoutManager.findFirstVisibleItemPosition() to list.computeVerticalScrollOffset() / 10
    }

    private fun firstVisiblePosition(): Int {
        var position = -1
        app.onActivity { activity ->
            val list = activity.findViewById<RecyclerView>(GameCatalogR.id.game_recycler_view)
            position = (list.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        }
        return position
    }

    private companion object {
        val CATALOG = GameCatalogR.id.game_screen_game_game_catalog_gamecatalogfragment
        val DETAILS = GameDetailsR.id.game_screen_game_game_details_gamedetailsfragment
        val SEARCH = GameSearchR.id.game_screen_game_game_search_gamesearchfragment
        val FAVORITES = GameFavoritesR.id.game_screen_game_game_favorites_gamefavoritesfragment

        /** games.json, in "popular now" order (the demo returns them as listed). */
        val DEMO_GAMES = listOf(
            "The Witcher 3: Wild Hunt", "Grand Theft Auto V", "Hollow Knight", "Elden Ring", "Stardew Valley",
        )
        val FIRST_GAME = DEMO_GAMES.first()
    }
}
