package com.rick.ecatalogs.anime.screen_anime.anime_details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.rick.ecatalogs.anime.data_anime.data.repository.browse.JikanBrowseRepository
import com.rick.ecatalogs.anime.data_anime.model.browse.AnimeEpisode
import com.rick.ecatalogs.anime.data_anime.model.browse.AnimeExtras
import com.rick.ecatalogs.anime.data_anime.model.browse.Broadcast
import com.rick.ecatalogs.anime.data_anime.model.browse.JikanItem
import com.rick.ecatalogs.data.ui_components.common.EcsPosterItem
import com.rick.ecatalogs.data.ui_components.common.EcsPosterRow
import com.rick.ecatalogs.data.ui_components.common.EcsSectionTitle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import javax.inject.Inject
import com.rick.ecatalogs.anime.screen_anime.common.R as CommonR

/**
 * Anime details beyond the stored list data (Jikan `anime/{id}/full`, episodes and
 * recommendations), loaded when the details screen opens. Requests are rate limited and kept
 * in memory for the session by the repository.
 */
@HiltViewModel
class AnimeExtrasViewModel @Inject constructor(
    private val repository: JikanBrowseRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /** Navigation argument (deep link `anime_details_fragment/{anime}`). */
    private val animeId: Int = checkNotNull(savedStateHandle["anime"])

    private val _extras = MutableStateFlow<AnimeExtras?>(null)
    val extras: StateFlow<AnimeExtras?> = _extras.asStateFlow()

    private val _recommendations = MutableStateFlow<List<JikanItem>>(emptyList())
    val recommendations: StateFlow<List<JikanItem>> = _recommendations.asStateFlow()

    private val _episodes = MutableStateFlow<EpisodesState>(EpisodesState.NotLoaded)
    val episodes: StateFlow<EpisodesState> = _episodes.asStateFlow()

    init {
        viewModelScope.launch {
            // Optional sections: on failure they simply don't show
            _extras.value = runCatching { repository.animeExtras(animeId) }.getOrNull()
            _recommendations.value =
                runCatching { repository.animeRecommendations(animeId) }.getOrDefault(emptyList())
        }
    }

    /** Loads the episodes the first time the sheet opens (or again after an error). */
    fun loadEpisodes() {
        if (_episodes.value is EpisodesState.Loaded || _episodes.value is EpisodesState.Loading) return
        _episodes.value = EpisodesState.Loading
        viewModelScope.launch {
            _episodes.value = try {
                EpisodesState.Loaded(repository.animeEpisodes(animeId))
            } catch (e: Exception) {
                EpisodesState.Error
            }
        }
    }
}

sealed interface EpisodesState {
    data object NotLoaded : EpisodesState
    data object Loading : EpisodesState
    data class Loaded(val episodes: List<AnimeEpisode>) : EpisodesState
    data object Error : EpisodesState
}

/** Where to watch, when it airs, its episodes, and what else to watch. */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AnimeExtrasSection(
    viewModel: AnimeExtrasViewModel,
    onOpenUrl: (String) -> Unit,
    onRecommendationClick: (Int) -> Unit,
) {
    val extras by viewModel.extras.collectAsStateWithLifecycle()
    val recommendations by viewModel.recommendations.collectAsStateWithLifecycle()
    val episodes by viewModel.episodes.collectAsStateWithLifecycle()
    var showEpisodes by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        extras?.broadcast?.let { broadcast ->
            localBroadcast(broadcast)?.let { (day, time) ->
                Text(
                    text = stringResource(CommonR.string.anime_screen_anime_common_airs, day, time),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }

        val streaming = extras?.streaming.orEmpty()
        if (streaming.isNotEmpty()) {
            EcsSectionTitle(stringResource(CommonR.string.anime_screen_anime_common_watch_on))
            FlowRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                streaming.forEach { service ->
                    AssistChip(onClick = { onOpenUrl(service.url) }, label = { Text(service.name) })
                }
            }
        }

        OutlinedButton(
            onClick = {
                showEpisodes = true
                viewModel.loadEpisodes()
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(stringResource(CommonR.string.anime_screen_anime_common_episodes))
        }

        EcsPosterRow(
            title = stringResource(CommonR.string.anime_screen_anime_common_if_you_liked),
            items = recommendations.map { EcsPosterItem(it.id, it.title, it.imageUrl) },
            onClick = { onRecommendationClick(it.id) },
        )
    }

    if (showEpisodes) {
        ModalBottomSheet(onDismissRequest = { showEpisodes = false }) {
            EpisodesSheet(episodes, onRetry = viewModel::loadEpisodes)
        }
    }
}

@Composable
private fun EpisodesSheet(state: EpisodesState, onRetry: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
        Text(
            text = stringResource(CommonR.string.anime_screen_anime_common_episodes),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )
        when (state) {
            EpisodesState.NotLoaded, EpisodesState.Loading -> LinearProgressIndicator(
                Modifier.fillMaxWidth().padding(20.dp)
            )
            EpisodesState.Error -> Text(
                text = stringResource(CommonR.string.anime_screen_anime_common_episodes_error),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable(onClick = onRetry).padding(20.dp),
            )
            is EpisodesState.Loaded -> if (state.episodes.isEmpty()) {
                Text(
                    text = stringResource(CommonR.string.anime_screen_anime_common_no_episodes),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(20.dp),
                )
            } else {
                LazyColumn {
                    items(state.episodes, key = { it.number }) { EpisodeRow(it) }
                }
            }
        }
    }
}

@Composable
private fun EpisodeRow(episode: AnimeEpisode) {
    val date = remember(episode.airDate) {
        episode.airDate?.let {
            runCatching {
                LocalDate.parse(it).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
            }.getOrNull()
        }
    }
    val score = episode.score?.let {
        stringResource(CommonR.string.anime_screen_anime_common_score, it)
    }
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)) {
        Text(
            text = stringResource(
                CommonR.string.anime_screen_anime_common_episode_title,
                episode.number, episode.title,
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = listOfNotNull(date, score).joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f, fill = false),
            )
            if (episode.filler) Tag(stringResource(CommonR.string.anime_screen_anime_common_filler))
            if (episode.recap) Tag(stringResource(CommonR.string.anime_screen_anime_common_recap))
        }
    }
}

@Composable
private fun Tag(text: String) {
    SuggestionChip(
        onClick = {},
        label = { Text(text, style = MaterialTheme.typography.labelSmall) },
        modifier = Modifier.padding(start = 8.dp),
    )
}

/**
 * The weekly slot converted from Japan time (Jikan's broadcast zone) to the phone's time zone,
 * as (day name, time), e.g. ("Saturday", "16:30"). Null if Jikan's values can't be read.
 */
internal fun localBroadcast(
    broadcast: Broadcast,
    zone: ZoneId = ZoneId.systemDefault(),
    locale: Locale = Locale.getDefault(),
): Pair<String, String>? = runCatching {
    // "Saturdays" -> SATURDAY
    val day = DayOfWeek.valueOf(broadcast.day.trim().removeSuffix("s").uppercase(Locale.ROOT))
    val time = LocalTime.parse(broadcast.time.trim())
    val japan = ZonedDateTime.now(ZoneId.of(broadcast.timezone))
        .with(TemporalAdjusters.nextOrSame(day))
        .with(time)
    val local = japan.withZoneSameInstant(zone)
    local.dayOfWeek.getDisplayName(TextStyle.FULL, locale) to
        local.toLocalTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale))
}.getOrNull()
