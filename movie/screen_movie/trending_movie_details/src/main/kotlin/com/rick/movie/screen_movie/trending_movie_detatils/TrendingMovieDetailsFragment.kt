package com.rick.movie.screen_movie.trending_movie_detatils

import android.graphics.Color
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
import com.rick.data.model_movie.tmdb.movie.Genre
import com.rick.data.model_movie.tmdb.series.Creator
import com.rick.movie.screen_movie.common.util.provideGlide
import com.rick.movie.screen_movie.trending_movie_details.R
import com.rick.movie.screen_movie.trending_movie_details.databinding.MovieScreenMovieTrendingMovieDetailsFragmentTrendingMovieDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrendingMovieDetailsFragment : Fragment() {

    private var _binding: MovieScreenMovieTrendingMovieDetailsFragmentTrendingMovieDetailsBinding? =
        null
    private val binding get() = _binding!!
    private val viewModel: TrendingMovieDetailsViewModel by viewModels()

    private lateinit var similarsAdapter: MovieSimilarDetailsAdapter

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
        }

        binding.toolbar.apply {
            setNavigationIcon(R.drawable.movie_screen_movie_trending_movie_details_ic_arrow_back)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.bindState(
            uiState = viewModel.uiState.asLiveData()
        )

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
        similarsAdapter = MovieSimilarDetailsAdapter()
        listSimilars.layoutManager = similarLayoutManager
        listSimilars.adapter = similarsAdapter

        bindList(
            similarsAdapter, uiState
        )
    }

    private fun MovieScreenMovieTrendingMovieDetailsFragmentTrendingMovieDetailsBinding.bindList(
        similarDetailsAdapter: MovieSimilarDetailsAdapter,
        uiState: LiveData<MovieDetailsUiState>,
    ) {

        val noData = getString(R.string.movie_screen_movie_trending_movie_details_no_data)

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
                        provideGlide(movieImage, trendingMovie.image)
                    }
                    movieSummary.text = trendingMovie.overview

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
                        if (trendingMovie.releaseDate.isNotEmpty()) resources.getString(
                            R.string.movie_screen_movie_trending_movie_details_release_date,
                            trendingMovie.releaseDate
                        )
                        else resources.getString(
                            R.string.movie_screen_movie_trending_movie_details_release_date, noData
                        )
                    movieRuntime.text = resources.getString(
                        R.string.movie_screen_movie_trending_movie_details_runtime,
                        trendingMovie.runtime
                    )

                    movieBudget.text = resources.getString(
                        R.string.movie_screen_movie_trending_movie_details_budget,
                        trendingMovie.budget
                    )

                    movieRevenue.text = resources.getString(
                        R.string.movie_screen_movie_trending_movie_details_revenue,
                        trendingMovie.revenue
                    )

                    imdbChip.text = resources.getString(
                        R.string.movie_screen_movie_trending_movie_details_imdb_rating,
                        trendingMovie.popularity
                    )
                    movieDbChip.text =
                        resources.getString(
                            R.string.movie_screen_movie_trending_movie_details_imdb_rating,
                            trendingMovie.voteAverage
                        )

                    similarDetailsAdapter.similarsDiffer
                        .submitList(trendingMovie.similar)
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
