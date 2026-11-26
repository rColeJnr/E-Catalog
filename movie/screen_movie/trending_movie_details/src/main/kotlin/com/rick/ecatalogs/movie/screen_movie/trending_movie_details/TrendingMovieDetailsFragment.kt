package com.rick.ecatalogs.movie.screen_movie.trending_movie_details

import com.rick.ecatalogs.data.ui_components.common.TranslationToggle
import com.rick.ecatalogs.data.translation.TextTranslator
import android.graphics.Color
import com.rick.ecatalogs.data.ui_design.POSTER_KIND_MOVIE
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
import com.google.android.material.transition.MaterialContainerTransform
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.movie.data_movie.model.tmdb.movie.Genre
import com.rick.ecatalogs.movie.data_movie.model.tmdb.series.Creator
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_movie.TrendingMovie
import com.rick.ecatalogs.movie.screen_movie.common.bindExtras
import com.rick.ecatalogs.movie.screen_movie.common.personDetailsUri
import com.rick.ecatalogs.movie.screen_movie.common.logScreenView
import com.rick.ecatalogs.movie.screen_movie.common.logTrendingMovieOpened
import com.rick.ecatalogs.movie.screen_movie.common.util.getTmdbImageUrl
import com.rick.ecatalogs.movie.screen_movie.common.util.provideGlide
import com.rick.ecatalogs.movie.screen_movie.trending_movie_details.R
import com.rick.ecatalogs.movie.screen_movie.trending_movie_details.databinding.MovieScreenMovieTrendingMovieDetailsFragmentTrendingMovieDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class TrendingMovieDetailsFragment : Fragment() {

    private var _binding: MovieScreenMovieTrendingMovieDetailsFragmentTrendingMovieDetailsBinding? =
        null
    private val binding get() = _binding!!
    private val viewModel: TrendingMovieDetailsViewModel by viewModels()

    private lateinit var similarsAdapter: MovieSimilarDetailsAdapter

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
        _binding = MovieScreenMovieTrendingMovieDetailsFragmentTrendingMovieDetailsBinding.inflate(
            inflater,
            container,
            false
        )

        arguments?.let {
            val safeArgs = TrendingMovieDetailsFragmentArgs.fromBundle(it)
            viewModel.setMovieId(safeArgs.movie)
            ViewCompat.setTransitionName(binding.movieImage, posterTransitionName(POSTER_KIND_MOVIE, safeArgs.movie))
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

        analyticsHelper.logScreenView("trendingMovieDetails")
        return binding.root
    }

    private fun MovieScreenMovieTrendingMovieDetailsFragmentTrendingMovieDetailsBinding.bindState(
        uiState: LiveData<MovieDetailsUiState>,
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
            MovieSimilarDetailsAdapter(this@TrendingMovieDetailsFragment::onSimilarClick)
        listSimilars.layoutManager = similarLayoutManager
        listSimilars.adapter = similarsAdapter

        bindList(
            similarsAdapter, uiState
        )
    }

    private fun onSimilarClick(similar: TrendingMovie) {
        analyticsHelper.logTrendingMovieOpened(similar.id.toString())
        val uri = Uri.parse("com.rick.ecs://trending_movie_details_fragment/${similar.id}")
        findNavController().navigateWithMotion(uri)
    }

    private fun MovieScreenMovieTrendingMovieDetailsFragmentTrendingMovieDetailsBinding.bindList(
        similarDetailsAdapter: MovieSimilarDetailsAdapter,
        uiState: LiveData<MovieDetailsUiState>,
    ) {

        val noData = getString(R.string.movie_screen_movie_trending_movie_details_no_data)
        val translation = TranslationToggle(showTranslation, movieSummary, textTranslator)

        uiState.observe(viewLifecycleOwner) { state ->

            when (state) {
                is MovieDetailsUiState.Error -> {
                    detailsProgressBar.visibility = View.GONE
                    if (state.msg.isNullOrBlank()) detailsErrorMessage.visibility = View.GONE
                    else detailsErrorMessage.visibility = View.VISIBLE
                }

                MovieDetailsUiState.Loading -> {
                    detailsProgressBar.visibility = View.VISIBLE
                }

                is MovieDetailsUiState.Success -> {
                    detailsProgressBar.visibility = View.GONE
                    val trendingMovie = state.movie
                    movieTitle.text = trendingMovie.title
                    if (trendingMovie.image.isNotEmpty()) {
                        provideGlide(movieImage, getTmdbImageUrl(trendingMovie.image), onReady = { startPostponedEnterTransition() })
                    }
                    translation.bind(trendingMovie.overview.trim())
                    if (trendingMovie.adult) {
                        movieAdult.text = resources.getString(
                            R.string.movie_screen_movie_trending_movie_details_adult_content, "Yes"
                        )
                    } else {
                        movieAdult.text = resources.getString(
                            R.string.movie_screen_movie_trending_movie_details_adult_content, "No"
                        )
                    }
                    movieGenres.text =
                        resources.getString(
                            R.string.movie_screen_movie_trending_movie_details_genres,
                            stringFromList(trendingMovie.genres)
                        )

                    moviePublicationDate.text =
                        if (trendingMovie.releaseDate.isNotEmpty()) {
                            try {
                                val inputFormatter = java.time.format.DateTimeFormatter.ofPattern(
                                    "yyyy-MM-dd",
                                    Locale.ENGLISH
                                )
                                val date = java.time.LocalDate.parse(
                                    trendingMovie.releaseDate,
                                    inputFormatter
                                )

                                val outputFormatter = java.time.format.DateTimeFormatter.ofPattern(
                                    "d 'of' MMMM, yyyy",
                                    Locale.getDefault()
                                )

                                resources.getString(
                                    R.string.movie_screen_movie_trending_movie_details_release_date,
                                    date.format(outputFormatter)
                                )
                            } catch (e: Exception) {
                                resources.getString(
                                    R.string.movie_screen_movie_trending_movie_details_release_date,
                                    trendingMovie.releaseDate
                                )
                            }
                        } else {
                            resources.getString(
                                R.string.movie_screen_movie_trending_movie_details_release_date,
                                noData
                            )
                        }
                    movieRuntime.text = resources.getString(
                        R.string.movie_screen_movie_trending_movie_details_runtime,
                        trendingMovie.runtime
                    )

                    movieBudget.text = resources.getString(
                        R.string.movie_screen_movie_trending_movie_details_budget,
                        trendingMovie.budget.toDouble() / 1_000_000.0
                    )

                    movieRevenue.text = resources.getString(
                        R.string.movie_screen_movie_trending_movie_details_revenue,
                        trendingMovie.revenue.toDouble() / 1_000_000.0
                    )

                    imdbChip.text = resources.getString(
                        R.string.movie_screen_movie_trending_movie_details_imdb_rating,
                        trendingMovie.popularity.toInt()
                    )
                    movieDbChip.text =
                        resources.getString(
                            R.string.movie_screen_movie_trending_movie_details_db_rating,
                            trendingMovie.voteAverage
                        )

                    similarDetailsAdapter.similarsDiffer
                        .submitList(trendingMovie.similar)
                    detailsExtras.bindExtras(trendingMovie.extras) { member ->
                        findNavController().navigateWithMotion(personDetailsUri(member.id))
                    }
                }
            }
        }
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

private const val TAG = "trendingMovieDetailsFragment"
//val dummyImages = listOf(
//    Item(
//        "https://image.tmdb.org/t/p/original/8IB2e4r4oVhHnANbnm7O3Tj6tF8.jpg"
//    ),
//    Item(
//        "https://m.media-amazon.com/images/M/MV5BMjI0MTg3MzI0M15BMl5BanBnXkFtZTcwMzQyODU2Mw@@._V1_Ratio0.7273_AL_.jpg"
//    ),
//    Item(
//        "https://image.tmdb.org/t/p/original/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg"
//    )
//)
//val dummyActors = listOf(
//    Actor(
//        "nm0000138",
//        "https://m.media-amazon.com/images/M/MV5BMjI0MTg3MzI0M15BMl5BanBnXkFtZTcwMzQyODU2Mw@@._V1_Ratio0.7273_AL_.jpg",
//        "Leonardo DiCaprio",
//        "Cobb"
//    ),
//    Actor(
//        "nm0330687",
//        "https://m.media-amazon.com/images/M/MV5BMTY3NTk0NDI3Ml5BMl5BanBnXkFtZTgwNDA3NjY0MjE@._V1_Ratio0.7273_AL_.jpg",
//        "Joseph Gordon-Levitt",
//        "Arthur"
//    ),
//    Actor(
//        "nm0680983",
//        "https://m.media-amazon.com/images/M/MV5BNmNhZmFjM2ItNTlkNi00ZTQ4LTk3NzYtYTgwNTJiMTg4OWQzXkEyXkFqcGdeQXVyMTM1MjAxMDc3._V1_Ratio0.7273_AL_.jpg",
//        "Elliot Page",
//        "Ariadne (as Ellen Page)"
//    )
//)
//val dummySimilars = listOf(
//    Similar(
//        "tt0816692",
//        "Interstellar",
//        "https://m.media-amazon.com/images/M/MV5BZjdkOTU3MDktN2IxOS00OGEyLWFmMjktY2FiMmZkNWIyODZiXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_Ratio0.6763_AL_.jpg",
//        "8.6"
//    ),
//    Similar(
//        "tt0468569",
//        "The Dark Knight",
//        "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_Ratio0.6763_AL_.jpg",
//        "9.0"
//    ),
//    Similar(
//        "tt0137523",
//        "Fight Club",
//        "https://m.media-amazon.com/images/M/MV5BNDIzNDU0YzEtYzE5Ni00ZjlkLTk5ZjgtNjM3NWE4YzA3Nzk3XkEyXkFqcGdeQXVyMjUzOTY1NTc@._V1_Ratio0.6763_AL_.jpg",
//        "8.8"
//    )
//)

private const val MAX_POSTPONE_MILLIS = 600L
