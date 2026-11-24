package com.rick.ecatalogs.anime.screen_anime.manga_details

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.rick.ecatalogs.anime.data_anime.data.repository.browse.JikanBrowseRepository
import com.rick.ecatalogs.anime.data_anime.model.browse.JikanItem
import com.rick.ecatalogs.data.ui_components.common.EcsPosterItem
import com.rick.ecatalogs.data.ui_components.common.EcsPosterRow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.rick.ecatalogs.anime.screen_anime.common.R as CommonR

/** "If you liked this" for a manga (MyAnimeList user recommendations via Jikan). */
@HiltViewModel
class MangaRecommendationsViewModel @Inject constructor(
    repository: JikanBrowseRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /** Navigation argument (deep link `manga_details_fragment/{manga}`). */
    private val mangaId: Int = checkNotNull(savedStateHandle["manga"])

    private val _recommendations = MutableStateFlow<List<JikanItem>>(emptyList())
    val recommendations: StateFlow<List<JikanItem>> = _recommendations.asStateFlow()

    init {
        viewModelScope.launch {
            // Optional section: hidden if it can't be loaded
            _recommendations.value =
                runCatching { repository.mangaRecommendations(mangaId) }.getOrDefault(emptyList())
        }
    }
}

@Composable
fun MangaRecommendationsRow(
    viewModel: MangaRecommendationsViewModel,
    onClick: (Int) -> Unit,
) {
    val recommendations by viewModel.recommendations.collectAsStateWithLifecycle()
    EcsPosterRow(
        title = stringResource(CommonR.string.anime_screen_anime_common_if_you_liked),
        items = recommendations.map { EcsPosterItem(it.id, it.title, it.imageUrl) },
        onClick = { onClick(it.id) },
        modifier = Modifier.padding(bottom = 16.dp),
    )
}
