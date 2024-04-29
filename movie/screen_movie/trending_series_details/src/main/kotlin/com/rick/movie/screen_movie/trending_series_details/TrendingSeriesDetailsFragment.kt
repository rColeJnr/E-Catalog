package com.rick.movie.screen_movie.trending_series_details

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import com.rick.data.model_movie.tmdb.movie.Genre
import com.rick.data.model_movie.tmdb.series.Creator
import com.rick.movie.screen_movie.common.util.provideGlide
import com.rick.movie.screen_movie.trending_series_details.databinding.MovieScreenMovieTrendingSeriesDetailsFragmentTrendingSeriesDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrendingSeriesDetailsFragment : Fragment() {

    private var _binding: MovieScreenMovieTrendingSeriesDetailsFragmentTrendingSeriesDetailsBinding? =
        null
    private val binding get() = _binding!!
    private val viewModel: SeriesDetailsViewModel by viewModels()

    private lateinit var similarsAdapter: SeriesSimilarDetailsAdapter
    private var id: Int? = null

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
                inflater,
                container,
                false
            )

        arguments?.let {
            val safeArgs = TrendingSeriesDetailsFragmentArgs.fromBundle(it)
            viewModel.setSeriesId(safeArgs.series)
        }

//        val safeArgs: FragmentBArgs by navArgs()
//        val yourarg = safeArgs.yourarg

        binding.toolbar.apply {
            setNavigationIcon(R.drawable.movie_screen_movie_trending_series_details_ic_arrow_back)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.bindState(
            uiState = viewModel.uiState.asLiveData()
        )

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
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        similarsAdapter = SeriesSimilarDetailsAdapter()
        listSimilars.layoutManager = similarLayoutManager
        listSimilars.adapter = similarsAdapter

        bindList(
            similarsAdapter, uiState
        )
    }

    private fun MovieScreenMovieTrendingSeriesDetailsFragmentTrendingSeriesDetailsBinding.bindList(
        similarDetailsAdapter: SeriesSimilarDetailsAdapter,
        uiState: LiveData<SeriesDetailsUiState>,
    ) {

        val noData = getString(R.string.movie_screen_movie_trending_series_details_no_data)

        uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SeriesDetailsUiState.Error -> {
                    Log.e(TAG, "see ${state.msg}")
                    detailsProgressBar.visibility = View.GONE
                    if (state.msg.isNullOrBlank()) detailsErrorMessage.visibility = View.GONE
                    else detailsErrorMessage.visibility = View.VISIBLE
                }

                SeriesDetailsUiState.Loading -> detailsProgressBar.visibility = View.VISIBLE
                is SeriesDetailsUiState.Success -> {
                    detailsProgressBar.visibility = View.GONE
                    val series = state.series
                    tvTitle.text = series.name
                    if (series.image.isNotEmpty()) {
                        provideGlide(image, series.image)
                    }
                    summary.text = series.overview
                    popularity.text =
                        getString(
                            R.string.movie_screen_movie_trending_series_details_popularity,
                            series.popularity
                        )
                    adult.text = resources.getString(
                        R.string.movie_screen_movie_trending_series_details_adult_content,
                        if (series.adult) "True" else "False"
                    )
                    genres.text =
                        resources.getString(
                            R.string.movie_screen_movie_trending_series_details_genres,
                            stringFromList(series.genres)
                        )

                    firstAirDate.text =
                        resources.getString(
                            R.string.movie_screen_movie_trending_series_details_first_air_date,
                            series.firstAirDate
                        )
                    inProduction.text = resources.getString(
                        R.string.movie_screen_movie_trending_series_details_in_production,
                        if (series.inProduction) "yes" else "no"
                    )

                    lastAirDate.text =
                        resources.getString(
                            R.string.movie_screen_movie_trending_series_details_last_air_date,
                            series.lastAirDate
                        )

                    creator.text =
                        resources.getString(
                            R.string.movie_screen_movie_trending_series_details_creators,
                            stringFromList(series.createdBy)
                        )

                    numberOfSeasons.text =
                        getString(
                            R.string.movie_screen_movie_trending_series_details_number_of_seasons,
                            series.numberOfSeasons
                        )

                    imdbChip.text =
                        resources.getString(
                            R.string.movie_screen_movie_trending_series_details_imdb_rating,
                            series.popularity
                        )
                    movieDbChip.text = resources.getString(
                        R.string.movie_screen_movie_trending_series_details_imdb_rating,
                        series.voteAverage
                    )

                    similarDetailsAdapter.similarsDiffer.submitList(series.similar)

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

fun stringFromList(list: List<Any>): String {
    val buffer: StringBuilder = StringBuilder()
    list.forEach {
        if (it is Creator) {
            buffer.append((it).name)
            buffer.append(" ")
        } else {
            buffer.append((it as Genre).name)
            buffer.append(" ")
        }
    }
    return buffer.toString()
}

private const val TAG = "trendingSeriesDetailsFragment"
