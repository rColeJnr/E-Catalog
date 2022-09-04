package com.rick.screen_movie.details_screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.rick.data_movie.imdb.movie_model.Actor
import com.rick.data_movie.imdb.movie_model.IMDBMovie
import com.rick.data_movie.imdb.movie_model.Image
import com.rick.data_movie.imdb.movie_model.Similar
import com.rick.screen_movie.R
import com.rick.screen_movie.databinding.FragmentMovieDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var movie: IMDBMovie
    private val viewModel: DetailsViewModel by viewModels()
    private lateinit var imagesAdapter: DetailsImagesAdapter
    private lateinit var actorsAdapter: ActorDetailsAdapter
    private lateinit var similarsAdapter: SimilarDetailsAdapter

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)

//        arguments?.let {
//            val safeArgs = MovieDetailsFragmentArgs.fromBundle(it)
//            movie = safeArgs.movie
//        }

        binding.apply {
            movieSummary.text = getString(R.string.movie_summary)
            movieGenres.text = "action, adventures, romance"
            movieAwards.text = "grammy, emmy"
            moviePublicationDate.text = "12.05.2000"
            movieRuntime.text = "2.45"
            imdbChip.text = "8.8"
            rTomatoesChip.text = "tomatoes"
            movieDbChip.text = "7"
            movieBudget.text = "$1.232.454"
            movieOpenWeekendGross.text = "845.456"
            movieWorldWideGross.text = "2.345.343"
        }

        initAdapters()

        binding.bindState(

        )

        return binding.root
    }

    private fun initAdapters() {
        val glide = Glide.with(requireContext())
        imagesAdapter = DetailsImagesAdapter(glide)
        actorsAdapter = ActorDetailsAdapter(glide)
        similarsAdapter = SimilarDetailsAdapter(glide)
    }

    private fun FragmentMovieDetailsBinding.bindState(
        images: LiveData<List<Image>>,
        actors: LiveData<List<Actor>>,
        similars: LiveData<List<Similar>>
    ) {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val layoutManager2 = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val layoutManager3 = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
        images: LiveData<List<Image>>,
        actors: LiveData<List<Actor>>,
        similars: LiveData<List<Similar>>
    ) {

        lifecycleScope.launchWhenCreated {
            images.observe(viewLifecycleOwner) {
            }
            imagesAdapter.imagesDiffer.submitList(dummyImages)
            actorDetailsAdapter.actorsDiffer.submitList(dummyActors)
            actors.observe(viewLifecycleOwner) {
            }
            similarDetailsAdapter.similarsDiffer.submitList(dummySimilars)
            similars.observe(viewLifecycleOwner) {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}