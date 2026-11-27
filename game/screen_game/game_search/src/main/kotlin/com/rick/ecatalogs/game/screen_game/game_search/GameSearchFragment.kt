package com.rick.ecatalogs.game.screen_game.game_search

import com.rick.ecatalogs.data.ui_design.R as DesignR
import androidx.compose.ui.res.painterResource
import javax.inject.Inject
import com.rick.ecatalogs.game.screen_game.common.releaseYear
import com.rick.ecatalogs.game.screen_game.common.ratingText
import com.rick.ecatalogs.game.data_game.model.coverUrl
import com.rick.ecatalogs.game.data_game.model.IgdbImageSize
import com.rick.ecatalogs.data.ui_components.common.LocalTextTranslator
import com.rick.ecatalogs.data.ui_components.common.EcsSearchResultCard
import com.rick.ecatalogs.data.ui_components.common.EcsBackButton
import com.rick.ecatalogs.data.translation.TextTranslator
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextField
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.ecatalogs.data.ui_components.common.EcsTheme
import com.rick.ecatalogs.data.ui_components.common.RecentSearchesBody
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
import com.rick.ecatalogs.game.screen_game.common.GameDeepLinks
import dagger.hilt.android.AndroidEntryPoint
import com.rick.ecatalogs.game.screen_game.common.R as CommonR

/** Game search (Compose): IGDB results, plus the stored games that match when offline. */
@AndroidEntryPoint
class GameSearchFragment : Fragment() {

    private val viewModel: GameSearchViewModel by viewModels()

    @Inject
    lateinit var textTranslator: TextTranslator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val duration = resources.getInteger(
            com.rick.ecatalogs.data.ui_design.R.integer.data_ui_design_motion_duration_medium
        ).toLong()
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).setDuration(duration)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).setDuration(duration)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            EcsTheme {
                // "Show translation" under each summary, as on the other search screens
                CompositionLocalProvider(LocalTextTranslator provides textTranslator) {
                    GameSearchScreen(
                        viewModel = viewModel,
                        onBack = { findNavController().navigateUp() },
                        onOpenGame = { findNavController().navigateWithMotion(GameDeepLinks.details(it)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun GameSearchScreen(
    viewModel: GameSearchViewModel,
    onBack: () -> Unit,
    onOpenGame: (Int) -> Unit,
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
        ) {
            EcsBackButton(onClick = onBack, contentDescription = stringResource(CommonR.string.game_screen_game_common_back))
            // A plain field in the bar, like the XML search screens' toolbar input
            TextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                singleLine = true,
                placeholder = { Text(stringResource(R.string.game_screen_game_game_search_hint)) },
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.onSearchTriggered(query)
                    keyboard?.hide()
                }),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
            )
            // The toolbar's search action of the XML search screens (orange, as drawn)
            IconButton(onClick = {
                viewModel.onSearchTriggered(query)
                keyboard?.hide()
            }) {
                Icon(
                    painterResource(DesignR.drawable.data_ui_design_ic_search),
                    contentDescription = stringResource(R.string.game_screen_game_game_search_hint),
                    tint = Color.Unspecified,
                )
            }
        }
        when (val current = results) {
            GameSearchResults.Idle -> if (recentSearches.isNotEmpty()) {
                RecentSearchesBody(
                    recentSearchQueries = recentSearches,
                    onClearRecentSearches = viewModel::clearRecentSearches,
                    onRecentSearchClicked = viewModel::onSearchTriggered,
                )
            }
            GameSearchResults.Searching -> LinearProgressIndicator(Modifier.fillMaxWidth())
            is GameSearchResults.Found -> if (current.games.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(stringResource(CommonR.string.game_screen_game_common_nothing_found))
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(current.games, key = { it.game.id }) { item ->
                        val game = item.game
                        // The same card as the movie, series, anime and book search results
                        EcsSearchResultCard(
                            title = game.name,
                            imageUrl = game.coverUrl(IgdbImageSize.HD),
                            summary = game.summary,
                            facts = listOfNotNull(game.releaseYear(), game.ratingText()),
                            isFavorite = item.isFavorite,
                            favoriteDescription = stringResource(
                                if (item.isFavorite) CommonR.string.game_screen_game_common_remove_favorite
                                else CommonR.string.game_screen_game_common_add_favorite
                            ),
                            onFavoriteClick = { viewModel.setFavorite(game.id, !item.isFavorite) },
                            onClick = {
                                // A result the user opened is worth remembering
                                viewModel.onSearchTriggered(query)
                                onOpenGame(game.id)
                            },
                        )
                    }
                }
            }
        }
    }
}
