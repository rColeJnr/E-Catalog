package com.rick.screen_movie.trendingmovie_details_screen

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import com.rick.data.model_movie.tmdb.series.Creator
import com.rick.screen_movie.R
import com.rick.screen_movie.databinding.FragmentMovieDetailsBinding
import com.rick.screen_movie.util.provideGlide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieDetailsViewModel by viewModels()

    private lateinit var similarsAdapter: SimilarDetailsAdapter
    private var id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)

        arguments?.let {
            val safeArgs = MovieDetailsFragmentArgs.fromBundle(it)
            id = safeArgs.movie
        }

        viewModel.onEvent(MovieDetailsEvents.GetMovie(id!!))

//        binding.toolbar.apply {
//            setNavigationIcon(R.drawable.ic_arrow_back)
//            setNavigationOnClickListener {
//                findNavController().navigateUp()
//            }
//        }

        binding.bindState(
            movie = viewModel.movie,
            loading = viewModel.searchLoading,
            error = viewModel.searchError
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.detailsCardView.transitionName = getString(R.string.movie_detail_transition_name)
        binding.root.transitionName = getString(R.string.search_transition_name, id.toString())
    }

    private fun FragmentMovieDetailsBinding.bindState(
        movie: LiveData<MovieDetailsUiState.Movie>,
        loading: LiveData<MovieDetailsUiState.Loading>,
        error: LiveData<MovieDetailsUiState.Error>
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
        similarsAdapter = SimilarDetailsAdapter()
        listSimilars.layoutManager = similarLayoutManager
        listSimilars.adapter = similarsAdapter

        bindList(
            similarsAdapter, movie, loading, error
        )
    }

    private fun FragmentMovieDetailsBinding.bindList(
        similarDetailsAdapter: SimilarDetailsAdapter,
        movie: LiveData<MovieDetailsUiState.Movie>,
        loading: LiveData<MovieDetailsUiState.Loading>,
        error: LiveData<MovieDetailsUiState.Error>
    ) {

        val noData = getString(R.string.no_data)

        movie.observe(viewLifecycleOwner) { tmdb ->
            val trendingMovie = tmdb.movie
            movieTitle.text = trendingMovie.title
            if (trendingMovie.image.isNotEmpty()) {
                provideGlide(movieImage, trendingMovie.image)
            }
            movieSummary.text = trendingMovie.summary

            if (trendingMovie.adult) {
                movieAdult.text = resources.getString(
                    R.string.adult_content, "Yes"
                )
            } else {
                movieAdult.text = resources.getString(
                    R.string.adult_content, "No"
                )
            }
            movieGenres.text =
                resources.getString(R.string.genres, stringFromList(trendingMovie.genres))

            moviePublicationDate.text =
                if (trendingMovie.releaseDate.isNotEmpty()) resources.getString(
                    R.string.release_date, trendingMovie.releaseDate
                )
                else resources.getString(
                    R.string.release_date, noData
                )
            movieRuntime.text = resources.getString(R.string.runtime, trendingMovie.runtime)

            movieBudget.text = resources.getString(R.string.budget, trendingMovie.budget)

            movieRevenue.text = resources.getString(R.string.revenue, trendingMovie.revenue)

            imdbChip.text =
                resources.getString(
                    R.string.imdb_rating, trendingMovie.popularity.toString()
                )
            movieDbChip.text = resources.getString(R.string.db_rating, trendingMovie.voteAverage)

            similarDetailsAdapter.similarsDiffer.submitList(trendingMovie.similar.results.map { it.toSimilar() })
        }

        loading.observe(viewLifecycleOwner) { progressing ->
            if (progressing.loading) {
                detailsProgressBar.visibility = View.VISIBLE
            } else detailsProgressBar.visibility = View.GONE
        }

        error.observe(viewLifecycleOwner) { msg ->
            if (msg.msg.isNullOrBlank()) detailsErrorMessage.visibility = View.GONE
            else detailsErrorMessage.visibility = View.VISIBLE
            movieGenres.text = resources.getString(R.string.genres, noData)

            movieGenres.text = resources.getString(R.string.awards, noData)
            moviePublicationDate.text = resources.getString(
                R.string.release_date, noData
            )
            movieRuntime.text = resources.getString(
                R.string.runtime, 0
            )
            imdbChip.text = resources.getString(
                R.string.imdb_rating, noData
            )
            movieDbChip.text = resources.getString(
                R.string.db_rating, 0
            )
            movieBudget.text = resources.getString(
                R.string.budget, 0
            )
            movieRevenue.text = resources.getString(R.string.revenue, 0)

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
            buffer.append((it as com.rick.data.model_movie.tmdb.movie.Genre).name)
            buffer.append(" ")
        }
    }
    return buffer.toString()
}


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
