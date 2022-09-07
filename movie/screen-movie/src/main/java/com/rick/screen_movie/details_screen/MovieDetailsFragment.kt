package com.rick.screen_movie.details_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.rick.data_movie.imdb.movie_model.Actor
import com.rick.data_movie.imdb.movie_model.Item
import com.rick.data_movie.imdb.movie_model.Similar
import com.rick.screen_movie.databinding.FragmentMovieDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailsViewModel by viewModels()
    private lateinit var imagesAdapter: DetailsImagesAdapter
    private lateinit var actorsAdapter: ActorDetailsAdapter
    private lateinit var similarsAdapter: SimilarDetailsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)

        arguments?.let {
            val safeArgs = MovieDetailsFragmentArgs.fromBundle(it)
            viewModel.setMovieOrSeriesId(safeArgs.movieId)
        }

        viewModel.movingPictures.observe(viewLifecycleOwner) {
            binding.apply {
                movieSummary.text = it.plot
                movieGenres.text = it.genres
                movieAwards.text = it.awards
                moviePublicationDate.text = it.releaseDate
                movieRuntime.text = it.runtimeStr
                imdbChip.text = it.ratings.imDb
                rTomatoesChip.text = it.ratings.rottenTomatoes
                movieDbChip.text = it.ratings.theMovieDb
                movieBudget.text = it.boxOffice.budget
                movieOpenWeekendGross.text = it.boxOffice.openingWeekendUSA
                movieWorldWideGross.text = it.boxOffice.cumulativeWorldwideGross

                bindState(
                    it.images.items,
                    it.actorList,
                    it.similars
                )
            }
        }


        initAdapters()

        return binding.root
    }

    private fun initAdapters() {
        val glide = Glide.with(requireContext())
        imagesAdapter = DetailsImagesAdapter(glide)
        actorsAdapter = ActorDetailsAdapter(glide)
        similarsAdapter = SimilarDetailsAdapter(glide)
    }

    private fun FragmentMovieDetailsBinding.bindState(
        images: List<Item>,
        actors: List<Actor>,
        similars: List<Similar>
    ) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val layoutManager2 =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val layoutManager3 =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        listImages.layoutManager = layoutManager
        listActors.layoutManager = layoutManager2
        listSimilars.layoutManager = layoutManager3
        listImages.adapter = imagesAdapter
        listActors.adapter = actorsAdapter
        listSimilars.adapter = similarsAdapter

        bindList(
            imagesAdapter,
            actorsAdapter,
            similarsAdapter,
            images,
            actors,
            similars
        )
    }

    private fun FragmentMovieDetailsBinding.bindList(
        imagesAdapter: DetailsImagesAdapter,
        actorDetailsAdapter: ActorDetailsAdapter,
        similarDetailsAdapter: SimilarDetailsAdapter,
        images: List<Item>,
        actors: List<Actor>,
        similars: List<Similar>
    ) {

        lifecycleScope.launchWhenCreated {

            imagesAdapter.imagesDiffer.submitList(images)
            actorDetailsAdapter.actorsDiffer.submitList(actors)
            similarDetailsAdapter.similarsDiffer.submitList(similars)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}


//    val dummyImages = listOf(
//        Item(
//            "https://image.tmdb.org/t/p/original/8IB2e4r4oVhHnANbnm7O3Tj6tF8.jpg"
//        ),
//        Item(
//            "https://m.media-amazon.com/images/M/MV5BMjI0MTg3MzI0M15BMl5BanBnXkFtZTcwMzQyODU2Mw@@._V1_Ratio0.7273_AL_.jpg"
//        ),
//        Item(
//            "https://image.tmdb.org/t/p/original/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg"
//        )
//    )
//    val dummyActors = listOf(
//        Actor(
//            "nm0000138",
//            "https://m.media-amazon.com/images/M/MV5BMjI0MTg3MzI0M15BMl5BanBnXkFtZTcwMzQyODU2Mw@@._V1_Ratio0.7273_AL_.jpg",
//            "Leonardo DiCaprio",
//            "Cobb"
//        ),
//        Actor(
//            "nm0330687",
//            "https://m.media-amazon.com/images/M/MV5BMTY3NTk0NDI3Ml5BMl5BanBnXkFtZTgwNDA3NjY0MjE@._V1_Ratio0.7273_AL_.jpg",
//            "Joseph Gordon-Levitt",
//            "Arthur"
//        ),
//        Actor(
//            "nm0680983",
//            "https://m.media-amazon.com/images/M/MV5BNmNhZmFjM2ItNTlkNi00ZTQ4LTk3NzYtYTgwNTJiMTg4OWQzXkEyXkFqcGdeQXVyMTM1MjAxMDc3._V1_Ratio0.7273_AL_.jpg",
//            "Elliot Page",
//            "Ariadne (as Ellen Page)"
//        )
//    )
//    val dummySimilars = listOf(
//        Similar(
//            "tt0816692",
//            "Interstellar",
//            "https://m.media-amazon.com/images/M/MV5BZjdkOTU3MDktN2IxOS00OGEyLWFmMjktY2FiMmZkNWIyODZiXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_Ratio0.6763_AL_.jpg",
//            "8.6"
//        ),
//        Similar(
//            "tt0468569",
//            "The Dark Knight",
//            "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_Ratio0.6763_AL_.jpg",
//            "9.0"
//        ),
//        Similar(
//            "tt0137523",
//            "Fight Club",
//            "https://m.media-amazon.com/images/M/MV5BNDIzNDU0YzEtYzE5Ni00ZjlkLTk5ZjgtNjM3NWE4YzA3Nzk3XkEyXkFqcGdeQXVyMjUzOTY1NTc@._V1_Ratio0.6763_AL_.jpg",
//            "8.8"
//        )
//    )
