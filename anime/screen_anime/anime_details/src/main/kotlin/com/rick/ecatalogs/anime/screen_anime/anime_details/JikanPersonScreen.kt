package com.rick.ecatalogs.anime.screen_anime.anime_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.ecatalogs.anime.data_anime.data.repository.browse.JikanBrowseRepository
import com.rick.ecatalogs.anime.data_anime.model.browse.JikanPerson
import com.rick.ecatalogs.anime.data_anime.model.browse.VoiceRole
import com.rick.ecatalogs.anime.screen_anime.common.jikanDetailsUri
import com.rick.ecatalogs.data.ui_components.common.EcsSectionTitle
import com.rick.ecatalogs.data.ui_components.common.EcsTheme
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject
import com.rick.ecatalogs.anime.screen_anime.common.R as CommonR

/** A voice actor: photo, details, and the anime they voiced (Compose). */
@AndroidEntryPoint
class JikanPersonFragment : Fragment() {

    private val viewModel: JikanPersonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val duration = resources.getInteger(
            com.rick.ecatalogs.data.ui_design.R.integer.data_ui_design_motion_duration_medium
        ).toLong()
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).setDuration(duration)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).setDuration(duration)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            EcsTheme {
                JikanPersonScreen(
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

@HiltViewModel
class JikanPersonViewModel @Inject constructor(
    private val repository: JikanBrowseRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /** Navigation argument (deep link `jikan_person/{person}`). */
    private val personId: Int = checkNotNull(savedStateHandle["person"])

    private val _state = MutableStateFlow<PersonState>(PersonState.Loading)
    val state: StateFlow<PersonState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.value = PersonState.Loading
        viewModelScope.launch {
            _state.value = try {
                PersonState.Loaded(repository.person(personId))
            } catch (e: Exception) {
                PersonState.Error
            }
        }
    }
}

sealed interface PersonState {
    data object Loading : PersonState
    data class Loaded(val person: JikanPerson) : PersonState
    data object Error : PersonState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JikanPersonScreen(
    viewModel: JikanPersonViewModel,
    onBack: () -> Unit,
    onAnimeClick: (Int) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = (state as? PersonState.Loaded)?.person?.name.orEmpty(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
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
        when (val current = state) {
            PersonState.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
            PersonState.Error -> Text(
                text = stringResource(CommonR.string.anime_screen_anime_common_person_error),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = viewModel::load).padding(24.dp),
            )
            is PersonState.Loaded -> PersonContent(current.person, onAnimeClick)
        }
    }
}

@Composable
private fun PersonContent(person: JikanPerson, onAnimeClick: (Int) -> Unit) {
    var aboutExpanded by rememberSaveable { mutableStateOf(false) }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(6.dp)) {
                    AsyncImage(
                        model = person.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(112.dp).clip(CircleShape),
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(person.name, style = MaterialTheme.typography.headlineSmall)
                        person.nativeName?.let {
                            Text(it, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                        }
                        person.birthday?.let { date ->
                            val formatted = runCatching {
                                LocalDate.parse(date).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                            }.getOrDefault(date)
                            Text(
                                stringResource(CommonR.string.anime_screen_anime_common_born, formatted),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
                if (person.about.isNotBlank()) {
                    Text(
                        text = person.about,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (aboutExpanded) Int.MAX_VALUE else ABOUT_LINES,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
                    )
                    TextButton(onClick = { aboutExpanded = !aboutExpanded }) {
                        Text(
                            stringResource(
                                if (aboutExpanded) CommonR.string.anime_screen_anime_common_read_less
                                else CommonR.string.anime_screen_anime_common_read_more
                            )
                        )
                    }
                }
                if (person.roles.isNotEmpty()) {
                    EcsSectionTitle(
                        stringResource(CommonR.string.anime_screen_anime_common_roles),
                        Modifier.padding(start = 0.dp),
                    )
                }
            }
        }
        items(person.roles, key = { it.animeId }) { role -> RoleCard(role, onAnimeClick) }
    }
}

@Composable
private fun RoleCard(role: VoiceRole, onAnimeClick: (Int) -> Unit) {
    Column(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onAnimeClick(role.animeId) }
    ) {
        AsyncImage(
            model = role.animeImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f).clip(RoundedCornerShape(12.dp)),
        )
        Text(
            text = role.animeTitle,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp),
        )
        Text(
            text = role.characterName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private const val ABOUT_LINES = 6
