package com.rick.ecatalogs.game.screen_game.game_search

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.rick.ecatalogs.game.screen_game.common.GameListItem
import dagger.hilt.android.AndroidEntryPoint
import com.rick.ecatalogs.game.screen_game.common.R as CommonR

/** Game search (Compose): IGDB results, plus the stored games that match when offline. */
@AndroidEntryPoint
class GameSearchFragment : Fragment() {

    private val viewModel: GameSearchViewModel by viewModels()

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
                GameSearchScreen(
                    viewModel = viewModel,
                    onBack = { findNavController().navigateUp() },
                    onOpenGame = { findNavController().navigateWithMotion(GameDeepLinks.details(it)) },
                )
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
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(CommonR.string.game_screen_game_common_back),
                )
            }
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                singleLine = true,
                placeholder = { Text(stringResource(R.string.game_screen_game_game_search_hint)) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = stringResource(R.string.game_screen_game_game_search_clear),
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.onSearchTriggered(query)
                    keyboard?.hide()
                }),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
                    .focusRequester(focusRequester),
            )
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
                        GameListItem(
                            item = item,
                            onClick = { game ->
                                // A result the user opened is worth remembering
                                viewModel.onSearchTriggered(query)
                                onOpenGame(game.id)
                            },
                            onFavoriteClick = { game, isFavorite -> viewModel.setFavorite(game.id, isFavorite) },
                        )
                    }
                }
            }
        }
    }
}
