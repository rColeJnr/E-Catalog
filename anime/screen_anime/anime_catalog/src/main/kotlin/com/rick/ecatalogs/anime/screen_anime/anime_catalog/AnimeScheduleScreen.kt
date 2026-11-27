package com.rick.ecatalogs.anime.screen_anime.anime_catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.ecatalogs.anime.data_anime.data.repository.browse.JikanBrowseRepository
import com.rick.ecatalogs.anime.screen_anime.common.inZone
import com.rick.ecatalogs.anime.screen_anime.common.jikanDetailsUri
import com.rick.ecatalogs.anime.screen_anime.common.jikanName
import com.rick.ecatalogs.data.ui_components.common.EcsTheme
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import com.rick.ecatalogs.anime.screen_anime.common.R as CommonR

/** This week's airing schedule in the user's time zone (Compose). */
@AndroidEntryPoint
class AnimeScheduleFragment : Fragment() {

    private val viewModel: AnimeScheduleViewModel by viewModels()

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
                AnimeScheduleScreen(
                    viewModel = viewModel,
                    onBack = { findNavController().navigateUp() },
                    onAnimeClick = { id ->
                        findNavController().navigateWithMotion(jikanDetailsUri(id, isAnime = true))
                    },
                )
            }
        }
    }
}

/** A show on the selected day, at its local time. */
data class ScheduleEntry(
    val id: Int,
    val title: String,
    val imageUrl: String?,
    val localTime: LocalTime?,
    val score: Double?,
)

sealed interface ScheduleState {
    data object Loading : ScheduleState
    data class Loaded(val entries: List<ScheduleEntry>) : ScheduleState
    data object Error : ScheduleState
}

@HiltViewModel
class AnimeScheduleViewModel @Inject constructor(
    private val repository: JikanBrowseRepository,
) : ViewModel() {

    /** The next seven days, from today, in the user's time zone. */
    val days: List<LocalDate> = (0L until 7L).map { LocalDate.now().plusDays(it) }

    private val _selected = MutableStateFlow(0)
    val selected: StateFlow<Int> = _selected.asStateFlow()

    private val _state = MutableStateFlow<ScheduleState>(ScheduleState.Loading)
    val state: StateFlow<ScheduleState> = _state.asStateFlow()

    init {
        load()
    }

    fun select(index: Int) {
        _selected.value = index
        load()
    }

    /**
     * Jikan groups shows by their day in Japan. A Japanese day can spill into the day before or
     * after locally, so the neighbouring Japanese days are loaded too and every show is placed by
     * its converted time (the repository keeps each day for the session).
     */
    fun load() {
        val day = days[_selected.value]
        _state.value = ScheduleState.Loading
        viewModelScope.launch {
            _state.value = try {
                val zone = ZoneId.systemDefault()
                val japanDays = listOf(day.minusDays(1), day, day.plusDays(1)).map { it.dayOfWeek }
                val shows = japanDays
                    .map { japanDay -> async { japanDay to repository.schedule(japanDay.jikanName) } }
                    .awaitAll()
                    .flatMap { (japanDay, dayShows) -> dayShows.map { japanDay to it } }
                    .distinctBy { (_, show) -> show.id }
                val entries = shows.mapNotNull { (japanDay, show) ->
                    val local = show.broadcast?.inZone(zone)
                    when {
                        local != null && local.dayOfWeek == day.dayOfWeek ->
                            ScheduleEntry(show.id, show.title, show.imageUrl, local.toLocalTime(), show.score)
                        local == null && japanDay == day.dayOfWeek ->
                            ScheduleEntry(show.id, show.title, show.imageUrl, null, show.score)
                        else -> null
                    }
                }.sortedWith(compareBy(nullsLast<LocalTime>()) { it.localTime })
                ScheduleState.Loaded(entries)
            } catch (e: Exception) {
                ScheduleState.Error
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimeScheduleScreen(
    viewModel: AnimeScheduleViewModel,
    onBack: () -> Unit,
    onAnimeClick: (Int) -> Unit,
) {
    val selected by viewModel.selected.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = { Text(stringResource(CommonR.string.anime_screen_anime_common_schedule)) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(CommonR.string.anime_screen_anime_common_back),
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        )
        ScrollableTabRow(selectedTabIndex = selected, edgePadding = 12.dp) {
            viewModel.days.forEachIndexed { index, day ->
                Tab(
                    selected = index == selected,
                    onClick = { viewModel.select(index) },
                    text = {
                        Text(
                            if (index == 0) stringResource(CommonR.string.anime_screen_anime_common_today)
                            else day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                        )
                    },
                )
            }
        }
        when (val current = state) {
            ScheduleState.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
            ScheduleState.Error -> Text(
                text = stringResource(CommonR.string.anime_screen_anime_common_schedule_error),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = viewModel::load).padding(24.dp),
            )
            is ScheduleState.Loaded -> if (current.entries.isEmpty()) {
                Text(
                    text = stringResource(CommonR.string.anime_screen_anime_common_schedule_empty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(24.dp),
                )
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(current.entries, key = { it.id }) { ScheduleRow(it, onAnimeClick) }
                }
            }
        }
    }
}

@Composable
private fun ScheduleRow(entry: ScheduleEntry, onAnimeClick: (Int) -> Unit) {
    val time = entry.localTime?.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)).orEmpty()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAnimeClick(entry.id) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(64.dp),
        )
        AsyncImage(
            model = entry.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(width = 52.dp, height = 74.dp).clip(RoundedCornerShape(8.dp)),
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            entry.score?.let {
                Text(
                    text = stringResource(CommonR.string.anime_screen_anime_common_score, it),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
