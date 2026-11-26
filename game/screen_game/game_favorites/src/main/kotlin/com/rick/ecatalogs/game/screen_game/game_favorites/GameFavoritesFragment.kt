package com.rick.ecatalogs.game.screen_game.game_favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.ecatalogs.data.ui_components.common.EcsEmptyState
import com.rick.ecatalogs.data.ui_components.common.EcsTheme
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
import com.rick.ecatalogs.game.screen_game.common.GameDeepLinks
import com.rick.ecatalogs.game.screen_game.common.GameListItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/** Favorite games (Compose), with undo after a removal. */
@AndroidEntryPoint
class GameFavoritesFragment : Fragment() {

    private val viewModel: GameFavoritesViewModel by viewModels()

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
                GameFavoritesScreen(
                    viewModel = viewModel,
                    onOpenGame = { findNavController().navigateWithMotion(GameDeepLinks.details(it)) },
                )
            }
        }
    }
}

@Composable
private fun GameFavoritesScreen(viewModel: GameFavoritesViewModel, onOpenGame: (Int) -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val removedMessage = stringResource(R.string.game_screen_game_game_favorites_removed)
    val undo = stringResource(R.string.game_screen_game_game_favorites_undo)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val current = state) {
                GameFavoritesUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is GameFavoritesUiState.Loaded -> if (current.games.isEmpty()) {
                    EcsEmptyState()
                } else {
                    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                        items(current.games, key = { it.game.id }) { item ->
                            GameListItem(
                                item = item,
                                onClick = { onOpenGame(it.id) },
                                onFavoriteClick = { game, isFavorite ->
                                    viewModel.setFavorite(game.id, isFavorite)
                                    if (!isFavorite) scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = removedMessage,
                                            actionLabel = undo,
                                            duration = SnackbarDuration.Long,
                                        )
                                        if (result == SnackbarResult.ActionPerformed) viewModel.setFavorite(game.id, true)
                                    }
                                },
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }
                }
            }
        }
    }
}
