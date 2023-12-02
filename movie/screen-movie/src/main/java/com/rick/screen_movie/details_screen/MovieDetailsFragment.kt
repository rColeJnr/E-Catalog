package com.rick.screen_movie.details_screen

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
import com.rick.screen_movie.R
import com.rick.screen_movie.databinding.FragmentMovieDetailsBinding
import com.rick.screen_movie.util.provideGlide
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailsViewModel by viewModels()

    private lateinit var similarsAdapter: SimilarDetailsAdapter

    private var id by Delegates.notNull<Int>()
    private var type by Delegates.notNull<String>()

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
            id = safeArgs.id
            type = safeArgs.type
        }

        if (type == "Movie") { viewModel.onTriggerEvent(DetailsUiEvent.GetTrendingMovie(id!!)) }
        else { viewModel.onTriggerEvent(DetailsUiEvent.GetTrendingTv(id!!)) }

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
        movie: LiveData<DetailsUiState.Movie>,
        loading: LiveData<DetailsUiState.Loading>,
        error: LiveData<DetailsUiState.Error>
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
        val similarlayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        listSimilars.layoutManager = similarlayoutManager
        listSimilars.adapter = similarsAdapter

        bindList(
            similarsAdapter, movie, loading, error
        )
    }

    private fun FragmentMovieDetailsBinding.bindList(
        similarDetailsAdapter: SimilarDetailsAdapter,
        movie: LiveData<DetailsUiState.Movie>,
        loading: LiveData<DetailsUiState.Loading>,
        error: LiveData<DetailsUiState.Error>
    ) {

        val noData = getString(R.string.no_data)

        movie.observe(viewLifecycleOwner) { tmdb ->
            val trendingMovie = tmdb.movie
            movieTitle.text = trendingMovie.title
            if (trendingMovie.posterPath.isNotEmpty()) {
                provideGlide(movieImage, trendingMovie.posterPath)
            }
            movieSummary.text = trendingMovie.overview
            movieAdult.text = resources.getString(
                R.string.adult_content,
                if (trendingMovie.adult) "True" else "False"
            )
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

            similarDetailsAdapter.similarsDiffer.submitList(trendingMovie.similar.results)
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
        buffer.append(it.toString())
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
