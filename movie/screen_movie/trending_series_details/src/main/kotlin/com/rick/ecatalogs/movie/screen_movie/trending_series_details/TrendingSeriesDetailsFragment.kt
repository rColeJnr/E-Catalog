package com.rick.ecatalogs.movie.screen_movie.trending_series_details

import com.rick.ecatalogs.data.ui_components.common.TranslationToggle
import com.rick.ecatalogs.data.translation.TextTranslator
import com.rick.ecatalogs.data.ui_design.useHeroCarousel
import com.rick.ecatalogs.movie.screen_movie.common.R as CommonR
import com.rick.ecatalogs.movie.screen_movie.common.BrowseRowAdapter
import com.rick.ecatalogs.movie.screen_movie.common.BrowseItem
import com.rick.ecatalogs.movie.data_movie.model.tmdb.browse.Season
import androidx.core.view.isVisible
import android.graphics.Color
import com.rick.ecatalogs.data.ui_design.POSTER_KIND_SERIES
import com.rick.ecatalogs.data.ui_design.posterTransitionName
import androidx.core.view.ViewCompat
import com.rick.ecatalogs.data.ui_design.navigateWithMotion
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.android.material.transition.MaterialContainerTransform
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.movie.data_movie.model.tmdb.movie.Genre
import com.rick.ecatalogs.movie.data_movie.model.tmdb.series.Creator
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_series.TrendingSeries
import com.rick.ecatalogs.movie.screen_movie.common.bindExtras
import com.rick.ecatalogs.movie.screen_movie.common.personDetailsUri
import com.rick.ecatalogs.movie.screen_movie.common.logScreenView
import com.rick.ecatalogs.movie.screen_movie.common.logTrendingSeriesOpened
import com.rick.ecatalogs.movie.screen_movie.common.util.getTmdbImageUrl
import com.rick.ecatalogs.movie.screen_movie.common.util.provideGlide
import com.rick.ecatalogs.movie.screen_movie.trending_series_details.databinding.MovieScreenMovieTrendingSeriesDetailsFragmentTrendingSeriesDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class TrendingSeriesDetailsFragment : Fragment() {

    private var _binding: MovieScreenMovieTrendingSeriesDetailsFragmentTrendingSeriesDetailsBinding? =
        null
    private val binding get() = _binding!!
    private val viewModel: SeriesDetailsViewModel by viewModels()

    private lateinit var similarsAdapter: SeriesSimilarDetailsAdapter

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    @Inject
    lateinit var textTranslator: TextTranslator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
            duration = 400L
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding =
            MovieScreenMovieTrendingSeriesDetailsFragmentTrendingSeriesDetailsBinding.inflate(
                inflater, container, false
            )

        arguments?.let {
            val safeArgs = TrendingSeriesDetailsFragmentArgs.fromBundle(it)
            viewModel.setSeriesId(safeArgs.series)
            ViewCompat.setTransitionName(binding.image, posterTransitionName(POSTER_KIND_SERIES, safeArgs.series))
        }

        // Hold the poster morph until the image is loaded (or a short timeout), so it doesn't
        // land on an empty view.
        postponeEnterTransition()
        binding.root.postDelayed({ if (isAdded) startPostponedEnterTransition() }, MAX_POSTPONE_MILLIS)

        binding.toolbar.apply {
            setNavigationIcon(com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_back_icon)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.bindState(
            uiState = viewModel.uiState.asLiveData()
        )

        analyticsHelper.logScreenView("trendingSeriesDetails")

        return binding.root
    }

    private fun MovieScreenMovieTrendingSeriesDetailsFragmentTrendingSeriesDetailsBinding.bindState(
        uiState: LiveData<SeriesDetailsUiState>,
    ) {
//        val layoutManager = LinearLayoutManager(
//            requireContext(),
//            LinearLayoutManager.HORIZONTAL,
//            false
//        )
//        val layoutManager2 = LinearLayoutManager(
//            requireContext(),
//            LinearLayoutManager.HORIZONTAL,
//            false
//        )
        val similarLayoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )
        similarsAdapter =
            SeriesSimilarDetailsAdapter(this@TrendingSeriesDetailsFragment::onSimilarClick)
        listSimilars.layoutManager = similarLayoutManager
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(listSimilars)
        listSimilars.adapter = similarsAdapter

        bindList(
            similarsAdapter, uiState
        )
    }

    private fun onSimilarClick(series: TrendingSeries) {
        analyticsHelper.logTrendingSeriesOpened(series.id.toString())
        val uri = Uri.parse("com.rick.ecs://trending_series_details_fragment/${series.id}")
        findNavController().navigateWithMotion(uri)
    }

    private fun MovieScreenMovieTrendingSeriesDetailsFragmentTrendingSeriesDetailsBinding.bindList(
        similarDetailsAdapter: SeriesSimilarDetailsAdapter,
        uiState: LiveData<SeriesDetailsUiState>,
    ) {
        val translation = TranslationToggle(showTranslation, summary, textTranslator)

        uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SeriesDetailsUiState.Error -> {
                    detailsProgressBar.visibility = View.GONE
                    if (state.msg.isNullOrBlank()) detailsErrorMessage.visibility = View.GONE
                    else detailsErrorMessage.visibility = View.VISIBLE
                }

                SeriesDetailsUiState.Loading -> detailsProgressBar.visibility = View.VISIBLE

                is SeriesDetailsUiState.Success -> {
                    detailsProgressBar.visibility = View.GONE
                    val noData =
                        getString(R.string.movie_screen_movie_trending_series_details_no_data)
                    val series = state.series
                    tvTitle.text = series.name
                    if (series.image.isNotBlank()) {
                        provideGlide(image, getTmdbImageUrl(series.image), onReady = { startPostponedEnterTransition() })
                    }
                    translation.bind(series.overview.trim())

                    adult.text = resources.getString(
                        R.string.movie_screen_movie_trending_series_details_adult_content,
                        if (series.adult) "True" else "False"
                    )
                    genres.text = resources.getString(
                        R.string.movie_screen_movie_trending_series_details_genres,
                        stringFromList(series.genres)
                    )

                    firstAirDate.text =
                        if (series.firstAirDate.isNotEmpty()) {
                            try {
                                val inputFormatter = java.time.format.DateTimeFormatter.ofPattern(
                                    "yyyy-MM-dd",
                                    Locale.ENGLISH
                                )
                                val date = java.time.LocalDate.parse(
                                    series.firstAirDate,
                                    inputFormatter
                                )

                                val outputFormatter = java.time.format.DateTimeFormatter.ofPattern(
                                    "d 'of' MMMM, yyyy",
                                    Locale.getDefault()
                                )

                                resources.getString(
                                    R.string.movie_screen_movie_trending_series_details_first_air_date,
                                    date.format(outputFormatter)
                                )
                            } catch (e: Exception) {
                                resources.getString(
                                    R.string.movie_screen_movie_trending_series_details_first_air_date,
                                    series.firstAirDate
                                )
                            }
                        } else {
                            resources.getString(
                                R.string.movie_screen_movie_trending_series_details_first_air_date,
                                noData
                            )
                        }
                    inProduction.text = resources.getString(
                        R.string.movie_screen_movie_trending_series_details_in_production,
                        if (series.inProduction)
                            getString(R.string.movie_screen_movie_trending_series_details_yes)
                        else
                            getString(R.string.movie_screen_movie_trending_series_details_no)
                    )

                    creator.text = resources.getString(
                        R.string.movie_screen_movie_trending_series_details_creators,
                        stringFromList(series.createdBy)
                    )

                    numberOfSeasons.text = getString(
                        R.string.movie_screen_movie_trending_series_details_number_of_seasons,
                        series.numberOfSeasons
                    )

                    numberOfEpisodes.text = getString(
                        R.string.movie_screen_movie_trending_series_details_number_of_episodes,
                        series.numberOfEpisodes
                    )

                    epRuntime.text = getString(
                        R.string.movie_screen_movie_trending_series_details_runtime,
                        series.episodeRuntime
                    )

                    imdbChip.text = resources.getString(
                        R.string.movie_screen_movie_trending_series_details_imdb_rating,
                        series.voteAverage
                    )
                    movieDbChip.text = resources.getString(
                        R.string.movie_screen_movie_trending_series_details_popularity,
                        series.popularity
                    )

                    similarDetailsAdapter.similarsDiffer.submitList(series.similar)
                    detailsExtras.bindExtras(series.extras) { member ->
                        findNavController().navigateWithMotion(personDetailsUri(member.id))
                    }
                    bindSeasons(series.id, series.seasons)

                }
            }
        }
    }

    /** Seasons row; tapping a season opens its episodes in a bottom sheet. */
    private fun bindSeasons(seriesId: Int, seasons: List<Season>) {
        binding.seasonsSection.isVisible = seasons.isNotEmpty()
        binding.seasonsList.useHeroCarousel()
        val adapter = binding.seasonsList.adapter as? BrowseRowAdapter
            ?: BrowseRowAdapter(onItemClick = { item ->
                SeasonSheet.newInstance(seriesId, item.id, item.title)
                    .show(childFragmentManager, "season_${item.id}")
            }).also { binding.seasonsList.adapter = it }
        adapter.submitList(
            seasons.map { season ->
                BrowseItem(
                    id = season.number,
                    title = season.name,
                    posterPath = season.posterPath,
                    subtitle = listOfNotNull(
                        getString(CommonR.string.movie_screen_movie_common_episodes, season.episodeCount),
                        season.airDate?.take(4),
                    ).joinToString(" · "),
                )
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun stringFromList(list: List<Any>): String {
    val buffer: StringBuilder = StringBuilder()
    list.forEach {
        if (it is Creator) {
            buffer.append((it).name)
            buffer.append("; ")
        } else {
            buffer.append((it as Genre).name)
            buffer.append("; ")
        }
    }
    return buffer.toString()
}

private const val TAG = "trendingSeriesDetailsFragment"

private const val MAX_POSTPONE_MILLIS = 600L
