package com.rick.ecatalogs.movie.screen_movie.trending_series_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rick.ecatalogs.movie.data_movie.data.repository.browse.TmdbBrowseRepository
import com.rick.ecatalogs.movie.data_movie.model.tmdb.browse.Episode
import com.rick.ecatalogs.movie.data_movie.model.tmdb.browse.SeasonDetails
import com.rick.ecatalogs.movie.screen_movie.common.util.getTmdbImageUrl
import com.rick.ecatalogs.movie.screen_movie.common.util.provideGlide
import com.rick.ecatalogs.movie.screen_movie.trending_series_details.databinding.MovieScreenMovieTrendingSeriesDetailsEpisodeEntryBinding
import com.rick.ecatalogs.movie.screen_movie.trending_series_details.databinding.MovieScreenMovieTrendingSeriesDetailsSeasonSheetBinding
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
import com.rick.ecatalogs.movie.screen_movie.common.R as CommonR

/** One season's episodes, shown from the series details screen. */
@AndroidEntryPoint
class SeasonSheet : BottomSheetDialogFragment() {

    private var _binding: MovieScreenMovieTrendingSeriesDetailsSeasonSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SeasonViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = MovieScreenMovieTrendingSeriesDetailsSeasonSheetBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.seasonTitle.text = requireArguments().getString(ARG_NAME)
        val adapter = EpisodeAdapter()
        binding.seasonEpisodes.adapter = adapter
        binding.seasonMessage.setOnClickListener {
            if (viewModel.state.value is SeasonUiState.Error) viewModel.load()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.seasonLoading.isVisible = state is SeasonUiState.Loading
                    val season = (state as? SeasonUiState.Success)?.season
                    if (season != null && season.name.isNotBlank()) binding.seasonTitle.text = season.name
                    binding.seasonOverview.text = season?.overview
                    binding.seasonOverview.isVisible = !season?.overview.isNullOrBlank()
                    adapter.submitList(season?.episodes.orEmpty())
                    binding.seasonMessage.isVisible =
                        state is SeasonUiState.Error || season?.episodes?.isEmpty() == true
                    binding.seasonMessage.setText(
                        if (state is SeasonUiState.Error) CommonR.string.movie_screen_movie_common_season_error
                        else CommonR.string.movie_screen_movie_common_no_episodes
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        internal const val ARG_SERIES_ID = "seriesId"
        internal const val ARG_SEASON = "season"
        internal const val ARG_NAME = "name"

        fun newInstance(seriesId: Int, seasonNumber: Int, name: String) = SeasonSheet().apply {
            arguments = bundleOf(
                ARG_SERIES_ID to seriesId,
                ARG_SEASON to seasonNumber,
                ARG_NAME to name,
            )
        }
    }
}

@HiltViewModel
class SeasonViewModel @Inject constructor(
    private val browseRepository: TmdbBrowseRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val seriesId: Int = checkNotNull(savedStateHandle[SeasonSheet.ARG_SERIES_ID])
    private val seasonNumber: Int = checkNotNull(savedStateHandle[SeasonSheet.ARG_SEASON])

    private val _state = MutableStateFlow<SeasonUiState>(SeasonUiState.Loading)
    val state: StateFlow<SeasonUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.value = SeasonUiState.Loading
        viewModelScope.launch {
            _state.value = try {
                SeasonUiState.Success(browseRepository.season(seriesId, seasonNumber))
            } catch (e: Exception) {
                SeasonUiState.Error
            }
        }
    }
}

sealed interface SeasonUiState {
    data object Loading : SeasonUiState
    data class Success(val season: SeasonDetails) : SeasonUiState
    data object Error : SeasonUiState
}

private class EpisodeAdapter : ListAdapter<Episode, EpisodeAdapter.ViewHolder>(DIFF) {

    class ViewHolder(val binding: MovieScreenMovieTrendingSeriesDetailsEpisodeEntryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        MovieScreenMovieTrendingSeriesDetailsEpisodeEntryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episode = getItem(position)
        with(holder.binding) {
            val context = root.context
            episodeName.text = context.getString(
                R.string.movie_screen_movie_trending_series_details_episode_title,
                episode.number, episode.name,
            )
            episodeMeta.text = listOfNotNull(
                episode.airDate?.let {
                    runCatching {
                        LocalDate.parse(it).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                    }.getOrNull()
                },
                episode.runtime?.let {
                    context.getString(R.string.movie_screen_movie_trending_series_details_minutes, it)
                },
                episode.voteAverage.takeIf { it > 0 }?.let {
                    context.getString(CommonR.string.movie_screen_movie_common_rating, it)
                },
            ).joinToString(" · ")
            episodeOverview.text = episode.overview
            episodeOverview.isVisible = episode.overview.isNotBlank()
            episode.stillPath?.let { provideGlide(episodeStill, getTmdbImageUrl(it, size = "w300")) }
                ?: episodeStill.setImageDrawable(null)
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<Episode>() {
            override fun areItemsTheSame(old: Episode, new: Episode) = old.id == new.id
            override fun areContentsTheSame(old: Episode, new: Episode) = old == new
        }
    }
}
