package com.rick.ecatalogs.game.screen_game.game_details

import com.rick.ecatalogs.data.trailer.InlineTrailer
import com.rick.ecatalogs.data.ui_components.common.EcsBackButton
import com.rick.ecatalogs.data.ui_components.common.EcsGlassFavoriteButton
import com.rick.ecatalogs.data.trailer.TrailerActivity
import com.rick.ecatalogs.data.trailer.R as TrailerR
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.ecatalogs.data.translation.TextTranslator
import com.rick.ecatalogs.data.ui_components.common.EcsPosterRow
import com.rick.ecatalogs.data.ui_components.common.EcsSectionTitle
import com.rick.ecatalogs.data.ui_components.common.EcsTheme
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
import com.rick.ecatalogs.game.data_game.model.GameDetails
import com.rick.ecatalogs.game.data_game.model.IgdbImageSize
import com.rick.ecatalogs.game.data_game.model.UserGameDetails
import com.rick.ecatalogs.game.data_game.model.coverUrl
import com.rick.ecatalogs.game.data_game.model.igdbImageUrl
import com.rick.ecatalogs.game.screen_game.common.GameDeepLinks
import com.rick.ecatalogs.game.screen_game.common.asPosterItem
import com.rick.ecatalogs.game.screen_game.common.ratingText
import com.rick.ecatalogs.game.screen_game.common.releaseDateText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.rick.ecatalogs.data.ui_components.common.R as UiR
import com.rick.ecatalogs.game.screen_game.common.R as CommonR

/** Game details (Compose): fetched from IGDB each time it opens. */
@AndroidEntryPoint
class GameDetailsFragment : Fragment() {

    private val viewModel: GameDetailsViewModel by viewModels()

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
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                GameDetailsScreen(
                    state = state,
                    translator = textTranslator,
                    onBack = { findNavController().navigateUp() },
                    onRetry = viewModel::load,
                    onFavoriteChange = viewModel::setFavorite,
                    onOpenGame = { findNavController().navigateWithMotion(GameDeepLinks.details(it)) },
                    onOpenUrl = { requireContext().openUrl(it) },
                    onWatchTrailer = { videoId, title -> TrailerActivity.start(requireContext(), videoId, title) },
                )
            }
        }
    }
}

private fun Context.openUrl(url: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: ActivityNotFoundException) {
        // No browser or YouTube app: nothing to open it with
        Toast.makeText(this, R.string.game_screen_game_game_details_no_app, Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameDetailsScreen(
    state: GameDetailsUiState,
    translator: TextTranslator,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onFavoriteChange: (Boolean) -> Unit,
    onOpenGame: (Int) -> Unit,
    onOpenUrl: (String) -> Unit,
    onWatchTrailer: (videoId: String, title: String) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = {
                val name = (state as? GameDetailsUiState.Loaded)?.game?.details?.game?.name.orEmpty()
                Text(name, maxLines = 1, overflow = TextOverflow.Ellipsis)
            },
            // The app's back arrow and glass favorite, as on the other screens
            navigationIcon = {
                EcsBackButton(onClick = onBack, contentDescription = stringResource(CommonR.string.game_screen_game_common_back))
            },
            actions = {
                if (state is GameDetailsUiState.Loaded) {
                    val isFavorite = state.game.isFavorite
                    EcsGlassFavoriteButton(
                        isFavorite = isFavorite,
                        onClick = { onFavoriteChange(!isFavorite) },
                        contentDescription = stringResource(
                            if (isFavorite) CommonR.string.game_screen_game_common_remove_favorite
                            else CommonR.string.game_screen_game_common_add_favorite
                        ),
                        modifier = Modifier.padding(end = 12.dp),
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        )
        when (state) {
            GameDetailsUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            GameDetailsUiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                TextButton(onClick = onRetry) {
                    Text(stringResource(CommonR.string.game_screen_game_common_load_failed))
                }
            }
            is GameDetailsUiState.Loaded ->
                GameDetailsContent(state.game, translator, onOpenGame, onOpenUrl, onWatchTrailer)
        }
    }
}

@Composable
private fun GameDetailsContent(
    item: UserGameDetails,
    translator: TextTranslator,
    onOpenGame: (Int) -> Unit,
    onOpenUrl: (String) -> Unit,
    onWatchTrailer: (videoId: String, title: String) -> Unit,
) {
    val details = item.details
    val game = details.game
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        Header(details)
        details.trailerYoutubeId?.let { videoId ->
            Button(
                onClick = { onWatchTrailer(videoId, game.name) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.game_screen_game_game_details_watch_trailer))
            }
        }
        if (game.summary.isNotBlank()) {
            EcsSectionTitle(stringResource(R.string.game_screen_game_game_details_summary))
            TranslatableText(game.summary, translator)
        }
        if (details.storyline.isNotBlank()) {
            EcsSectionTitle(stringResource(R.string.game_screen_game_game_details_storyline))
            TranslatableText(details.storyline, translator)
        }
        val trailerId = details.trailerYoutubeId
        if (trailerId != null || details.screenshotIds.isNotEmpty()) {
            EcsSectionTitle(
                stringResource(
                    when {
                        trailerId == null -> R.string.game_screen_game_game_details_screenshots
                        details.screenshotIds.isEmpty() -> TrailerR.string.data_trailer_trailer
                        else -> R.string.game_screen_game_game_details_trailer_and_screenshots
                    }
                )
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // The trailer first; it plays here (its fullscreen button opens the trailer screen)
                if (trailerId != null) item(key = "trailer:$trailerId") {
                    InlineTrailer(
                        videoId = trailerId,
                        title = game.name,
                        isVisible = true,
                        modifier = Modifier
                            .height(160.dp)
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(12.dp)),
                    )
                }
                items(details.screenshotIds.distinct(), key = { "screenshot:$it" }) { imageId ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(igdbImageUrl(imageId, IgdbImageSize.SCREENSHOT_BIG))
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(160.dp)
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(12.dp)),
                    )
                }
            }
        }
        InfoRow(R.string.game_screen_game_game_details_platforms, game.platforms)
        InfoRow(R.string.game_screen_game_game_details_developers, details.developers)
        InfoRow(R.string.game_screen_game_game_details_publishers, details.publishers)
        EcsPosterRow(
            title = stringResource(R.string.game_screen_game_game_details_similar),
            items = details.similar.map { it.asPosterItem() },
            onClick = { onOpenGame(it.id) },
        )
        details.igdbUrl?.let { url ->
            OutlinedButton(
                onClick = { onOpenUrl(url) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            ) { Text(stringResource(R.string.game_screen_game_game_details_open_igdb)) }
        }
    }
}

@Composable
private fun Header(details: GameDetails) {
    val game = details.game
    Box(Modifier.fillMaxWidth()) {
        // Backdrop: the first screenshot, behind the cover
        details.screenshotIds.firstOrNull()?.let { imageId ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(igdbImageUrl(imageId, IgdbImageSize.HD))
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.35f,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
            )
        }
        Row(
            Modifier.padding(start = 16.dp, end = 16.dp, top = 72.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(game.coverUrl())
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
            )
            Column(Modifier.weight(1f).padding(top = 40.dp)) {
                Text(game.name, style = MaterialTheme.typography.headlineSmall)
                listOfNotNull(game.releaseDateText(), game.ratingText()).joinToString(" · ")
                    .takeIf { it.isNotEmpty() }
                    ?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                if (game.genres.isNotEmpty()) {
                    Text(
                        game.genres.joinToString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(titleRes: Int, values: List<String>) {
    if (values.isEmpty()) return
    EcsSectionTitle(stringResource(titleRes))
    Text(
        values.joinToString(),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
}

/** Text with a "show translation" button, like the XML screens' TranslationToggle. */
@Composable
private fun TranslatableText(text: String, translator: TextTranslator) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var translation by remember(text) { mutableStateOf<String?>(null) }
    var showingTranslation by remember(text) { mutableStateOf(false) }
    var translating by remember(text) { mutableStateOf(false) }
    Crossfade(targetState = if (showingTranslation) translation ?: text else text, label = "translation") {
        Text(
            it,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
    if (!translator.isAvailable) return
    TextButton(
        enabled = !translating,
        onClick = {
            when {
                showingTranslation -> showingTranslation = false
                translation != null -> showingTranslation = true
                else -> scope.launch {
                    translating = true
                    translation = translator.translate(text)
                    translating = false
                    if (translation != null) {
                        showingTranslation = true
                    } else {
                        Toast.makeText(context, UiR.string.data_ui_components_common_translation_failed, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        },
        modifier = Modifier.padding(horizontal = 8.dp),
    ) {
        Text(
            stringResource(
                when {
                    translating -> UiR.string.data_ui_components_common_translating
                    showingTranslation -> UiR.string.data_ui_components_common_show_original
                    else -> UiR.string.data_ui_components_common_show_translation
                }
            )
        )
    }
}
